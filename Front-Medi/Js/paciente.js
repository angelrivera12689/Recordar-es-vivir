

// Cargar pacientes al iniciar
document.addEventListener('DOMContentLoaded', loadPatients);

async function fetchPatients() {
    try {
        const response = await fetch(`${API_BASE}/api/patients`);
        if (response.ok) {
            patients = await response.json();
            return patients;
        }
        throw new Error('Error al cargar pacientes');
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al cargar pacientes', 'error');
        return [];
    }
}

async function loadPatients() {
    patients = await fetchPatients(); // 👈 esto asegura que se actualiza la variable global

    const container = document.getElementById('patientsContainer');

    if (patients.length === 0) {
        container.innerHTML = '<p class="no-data">No hay pacientes registrados</p>';
        return;
    }

    container.innerHTML = patients.map(patient => `
        <div class="card">
            <div class="card-header">
                <h4>${patient.firstName} ${patient.lastName}</h4>
                <div class="card-actions">
                    <button onclick="editPatient(${patient.id})" class="btn btn-small btn-secondary">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button onclick="confirmDelete('patient', ${patient.id})" class="btn btn-small btn-danger">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            <div class="card-body">
                <p><strong>Email:</strong> ${patient.email}</p>
                <p><strong>Teléfono:</strong> ${patient.phone || 'N/A'}</p>
                <p><strong>Estado:</strong> <span class="status-badge status-${patient.status?.toLowerCase()}">${patient.status}</span></p>
                <p><strong>Registro:</strong> ${formatDate(patient.registrationDate)}</p>
            </div>
        </div>
    `).join('');
}


function renderPatients(patientList) {
    const container = document.getElementById('patientsContainer');
    if (patientList.length === 0) {
        container.innerHTML = '<p class="no-data">No hay pacientes registrados</p>';
        return;
    }

    const limited = patientList.slice(0, 3);
    container.innerHTML = limited.map(patient => `
        <div class="card">
            <div class="card-header">
                <h4>${patient.firstName} ${patient.lastName}</h4>
                <div class="card-actions">
                    <button onclick="editPatient(${patient.id})" class="btn btn-small btn-secondary">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button onclick="confirmDelete('patient', ${patient.id})" class="btn btn-small btn-danger">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            <div class="card-body">
                <p><strong>Email:</strong> ${patient.email}</p>
                <p><strong>Teléfono:</strong> ${patient.phone || 'N/A'}</p>
                <p><strong>Estado:</strong> <span class="status-badge status-${patient.status?.toLowerCase()}">${patient.status}</span></p>
                <p><strong>Registro:</strong> ${formatDate(patient.registrationDate)}</p>
            </div>
        </div>
    `).join('');
}

async function loadPatients() {
    await fetchPatients();
    renderPatients(patients);
}

async function createPatient(patientData) {
    try {
        const response = await fetch(`${API_BASE}/api/patients`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(patientData)
        });

        const newPatient = await response.json();

        if (response.ok) {
            showAlert('Paciente creado exitosamente', 'success');
            patients.push(newPatient);
            renderPatients(patients);
            return true;
        } else {
            showAlert(newPatient.message || 'Error al crear paciente', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al crear paciente', 'error');
        return false;
    }
}

async function updatePatient(id, patientData) {
    try {
        const response = await fetch(`${API_BASE}/api/patients/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(patientData)
        });

        const updated = await response.json();

        if (response.ok) {
            showAlert('Paciente actualizado exitosamente', 'success');
            const index = patients.findIndex(p => p.id === id);
            if (index !== -1) {
                patients[index] = { ...patients[index], ...patientData };
                renderPatients(patients);
            }
            return true;
        } else {
            showAlert(updated.message || 'Error al actualizar paciente', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al actualizar paciente', 'error');
        return false;
    }
}

async function deletePatient(id) {
    try {
        const response = await fetch(`${API_BASE}/api/patients/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showAlert('Paciente eliminado exitosamente', 'success');
            patients = patients.filter(p => p.id !== id);
            renderPatients(patients);
            return true;
        } else {
            const result = await response.json();
            showAlert(result.message || 'Error al eliminar paciente', 'error');
            return false;
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al eliminar paciente', 'error');
        return false;
    }
}

function editPatient(id) {
    const patient = patients.find(p => p.id === id);
    if (!patient) return;

    currentEditId = id;

    const modalBody = document.getElementById('modalBody');
    modalBody.innerHTML = `
        <form id="editPatientForm">
            <div class="form-group">
                <label>Nombre</label>
                <input type="text" id="editPatientFirstName" value="${patient.firstName}" required>
            </div>
            <div class="form-group">
                <label>Apellido</label>
                <input type="text" id="editPatientLastName" value="${patient.lastName}" required>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" id="editPatientEmail" value="${patient.email}" required>
            </div>
            <div class="form-group">
                <label>Teléfono</label>
                <input type="tel" id="editPatientPhone" value="${patient.phone || ''}">
            </div>
            <div class="form-group">
                <label>Estado</label>
                <select id="editPatientStatus" required>
                    <option value="ACTIVE" ${patient.status === 'ACTIVE' ? 'selected' : ''}>Activo</option>
                    <option value="SUSPENDED" ${patient.status === 'SUSPENDED' ? 'selected' : ''}>Suspendido</option>
                </select>
            </div>
            <div class="modal-buttons">
                <button type="button" onclick="closeModal()" class="btn btn-secondary">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar</button>
            </div>
        </form>
    `;

    document.getElementById('modalTitle').textContent = 'Editar Paciente';
    document.getElementById('editModal').style.display = 'block';

    document.getElementById('editPatientForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        const updatedPatient = {
            firstName: document.getElementById('editPatientFirstName').value.trim(),
            lastName: document.getElementById('editPatientLastName').value.trim(),
            email: document.getElementById('editPatientEmail').value.trim(),
            phone: document.getElementById('editPatientPhone').value.trim(),
            status: document.getElementById('editPatientStatus').value
        };

        const success = await updatePatient(currentEditId, updatedPatient);

        if (success) {
    closeModal();
    loadPatients(); // 👈 aquí actualizas la lista en pantalla
}
    });
}

function confirmDelete(type, id) {
    if (confirm('¿Estás seguro de que deseas eliminar este paciente?')) {
        deletePatient(id);
    }
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
}

function showAlert(message, type) {
    alert(`${type.toUpperCase()}: ${message}`);
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString();
}
