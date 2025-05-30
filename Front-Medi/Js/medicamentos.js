
// Medicamentos
async function fetchMedications() {
    try {
        const response = await fetch(`${API_BASE}/medications`);
        if (response.ok) {
            medications = await response.json();
            return medications;
        }
        throw new Error('Error al cargar medicamentos');
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al cargar medicamentos', 'error');
        return [];
    }
}

async function createMedication(medicationData) {
    try {
        const response = await fetch(`${API_BASE}/medications`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(medicationData)
        });
        const result = await response.json();
        if (response.ok || response.status === 201) {
            showAlert('Medicamento creado exitosamente', 'success');
            loadMedications();
            return true;
        } else {
            showAlert(result.message || 'Error al crear medicamento', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al crear medicamento', 'error');
        return false;
    }
}

async function updateMedication(id, medicationData) {
    try {
        const response = await fetch(`${API_BASE}/medications/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(medicationData)
        });
        const result = await response.json();
        if (response.ok) {
            showAlert('Medicamento actualizado exitosamente', 'success');
            loadMedications();
            return true;
        } else {
            showAlert(result.message || 'Error al actualizar medicamento', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al actualizar medicamento', 'error');
        return false;
    }
}

async function deleteMedication(id) {
    try {
        const response = await fetch(`${API_BASE}/medications/${id}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            showAlert('Medicamento eliminado exitosamente', 'success');
            loadMedications();
            return true;
        } else {
            const result = await response.json();
            showAlert(result.message || 'Error al eliminar medicamento', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al eliminar medicamento', 'error');
        return false;
    }
}
function editMedication(id) {
    const medication = medications.find(m => m.id === id);
    if (!medication) return;

    currentEditId = id;
    currentEditType = 'medication';

    const modalBody = document.getElementById('modalBody');

    modalBody.innerHTML = `
        <form id="editMedicationForm">
            <div class="form-group">
                <label>Nombre</label>
                <input type="text" id="editMedicationName" value="${medication.name || ''}" required>
            </div>
            <div class="form-group">
                <label>Dosis</label>
                <input type="text" id="editMedicationDose" value="${medication.dose || ''}" required>
            </div>
            <div class="form-group">
                <label>Frecuencia (horas)</label>
                <input type="number" id="editMedicationFrequencyHours" value="${medication.frequencyHours || ''}" min="1" required>
            </div>
            <div class="form-group">
                <label>Hora de inicio</label>
                <input type="datetime-local" id="editMedicationStartTime" value="${formatDateTimeForInput(medication.startTime)}" required>
            </div>
            <div class="form-group">
                <label>Duración del tratamiento (días)</label>
                <input type="number" id="editMedicationTreatmentDurationDays" value="${medication.treatmentDurationDays || ''}" min="1" required>
            </div>
            <div class="form-group">
                <label>Instrucciones</label>
                <textarea id="editMedicationInstructions" rows="3">${medication.instructions || ''}</textarea>
            </div>
            <div class="modal-buttons">
                <button type="button" onclick="closeModal()" class="btn btn-secondary">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar</button>
            </div>
        </form>
    `;

    document.getElementById('modalTitle').textContent = `Editar Medicamento - ${medication.name}`;
    document.getElementById('editModal').style.display = 'block';

    document.getElementById('editMedicationForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const updatedMedication = {
            name: document.getElementById('editMedicationName').value.trim(),
            dose: document.getElementById('editMedicationDose').value.trim(),
            frequencyHours: parseInt(document.getElementById('editMedicationFrequencyHours').value, 10),
            startTime: document.getElementById('editMedicationStartTime').value,
            treatmentDurationDays: parseInt(document.getElementById('editMedicationTreatmentDurationDays').value, 10),
            instructions: document.getElementById('editMedicationInstructions').value.trim()
        };

        const success = await updateMedication(id, updatedMedication);
        if (success) {
            closeModal();
        }
    });
}

// Formatea fecha y hora para input datetime-local (yyyy-MM-ddTHH:mm)
function formatDateTimeForInput(dateTimeString) {
    if (!dateTimeString) return '';
    const d = new Date(dateTimeString);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
    document.getElementById('modalBody').innerHTML = '';
}
