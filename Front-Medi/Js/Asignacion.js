
async function fetchAssignments() {
    try {
        const response = await fetch(`${API_BASE}/assignments`);
        if (response.ok) {
            assignments = await response.json();
            return assignments;
        }
        throw new Error('Error al cargar asignaciones');
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al cargar asignaciones', 'error');
        return [];
    }
}

async function createAssignment(assignmentData) {
    try {
        const response = await fetch(`${API_BASE}/assignments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(assignmentData)
        });
        const result = await response.json();
        if (response.ok) {
            showAlert('Asignación creada exitosamente', 'success');
            loadAssignments();
            return true;
        } else {
            showAlert(result.message || 'Error al crear asignación', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al crear asignación', 'error');
        return false;
    }
}

async function updateAssignment(id, assignmentData) {
    try {
        const response = await fetch(`${API_BASE}/assignments/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(assignmentData)
        });
        const result = await response.json();
        if (response.ok) {
            showAlert('Asignación actualizada exitosamente', 'success');
            loadAssignments();
            return true;
        } else {
            showAlert(result.message || 'Error al actualizar asignación', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al actualizar asignación', 'error');
        return false;
    }
}

async function deleteAssignment(id) {
    try {
        const response = await fetch(`${API_BASE}/assignments/${id}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            showAlert('Asignación eliminada exitosamente', 'success');
            loadAssignments();
            return true;
        } else {
            const result = await response.json();
            showAlert(result.message || 'Error al eliminar asignación', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al eliminar asignación', 'error');
        return false;
    }
}

function editAssignment(id) {
    const assignment = assignments.find(a => a.id === id);
    if (!assignment) return;

    currentEditId = id;
    currentEditType = 'assignment';

    const modalBody = document.getElementById('modalBody');
    modalBody.innerHTML = `
        <form id="editAssignmentForm">
            <div class="form-group">
                <label>Paciente</label>
                <select id="editAssignmentPatient" required>
                    <option value="">Seleccionar paciente...</option>
                    ${patients.map(patient => 
                        `<option value="${patient.id}" ${patient.id === assignment.patientId ? 'selected' : ''}>${patient.firstName} ${patient.lastName}</option>`
                    ).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Medicamento</label>
                <select id="editAssignmentMedicine" required>
                    <option value="">Seleccionar medicamento...</option>
                    ${medications.map(medication => 
                        `<option value="${medication.id}" ${medication.id === assignment.medicineId ? 'selected' : ''}>${medication.name}</option>`
                    ).join('')}
                </select>
            </div>
            <div class="form-group">
                <label>Frecuencia en minutos</label>
                <input type="number" id="editAssignmentFrequencyMinutes" value="${assignment.frequencyMinutes}" min="1" required>
            </div>
            <div class="form-group">
                <label>Fecha y hora de inicio</label>
                <input type="datetime-local" id="editAssignmentStartTime" value="${formatDateForInput(assignment.startTime)}" required>
            </div>
            <div class="modal-buttons">
                <button type="button" onclick="closeModal()" class="btn btn-secondary">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar</button>
            </div>
        </form>
    `;

    document.getElementById('modalTitle').textContent = 'Editar Asignación';
    document.getElementById('editModal').style.display = 'block';

    // ⬇️⬇️ Aquí añadimos el evento submit que guarda los cambios
    document.getElementById('editAssignmentForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const updatedAssignment = {
            patientId: parseInt(document.getElementById('editAssignmentPatient').value, 10),
            medicineId: parseInt(document.getElementById('editAssignmentMedicine').value, 10),
            frequencyMinutes: parseInt(document.getElementById('editAssignmentFrequencyMinutes').value, 10),
            startTime: document.getElementById('editAssignmentStartTime').value
        };

        const success = await updateAssignment(id, updatedAssignment);
        if (success) closeModal();
    });
}
