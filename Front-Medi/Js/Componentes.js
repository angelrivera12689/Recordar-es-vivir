// MediTrack - Sistema de Gestión de Medicamentos

// Configuración de la API base
const API_BASE = 'http://localhost:8080';

// Estado global
let currentEditId = null;
let currentEditType = null;

// Datos en memoria
let patients = [];
let medications = [];
let assignments = [];
let logs = [];

// ================== UTILIDADES GENERALES ==================

function showAlert(message, type = 'info') {
    const alertsContainer = document.getElementById('alerts');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.innerHTML = `
        <span>${message}</span>
        <button onclick="this.parentElement.remove()" class="alert-close">&times;</button>
    `;
    alertsContainer.appendChild(alert);
    setTimeout(() => { if (alert.parentElement) alert.remove(); }, 5000);
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit'
    });
}

function formatDateForInput(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16);
}

// ================== NAVEGACIÓN ENTRE SECCIONES ==================

function showSection(sectionName) {
    // Ocultar todas las secciones
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    // Quitar clase activa de tabs
    document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));

    // Mostrar la sección solicitada
    const targetSection = document.getElementById(sectionName);
    if (targetSection) targetSection.classList.add('active');

    // Marcar la pestaña activa
    const activeTab = document.querySelector(`[onclick="showSection('${sectionName}')"]`);
    if (activeTab) activeTab.classList.add('active');

    // Cargar datos según sección
    switch (sectionName) {
        case 'dashboard': loadDashboardStats(); break;
        case 'patients': loadPatients(); break;
        case 'medications': loadMedications(); break;
        case 'assignments':
            loadAssignments();
            loadPatientsForSelect();
            loadMedicationsForSelect();
            break;
        case 'logs': loadLogs(); break;
    }
}


// ================== FUNCIONES DE CARGA ==================

async function fetchPatients() {
    const res = await fetch(`${API_BASE}/api/patients`);
    patients = await res.json();
    return patients;
}

async function fetchMedications() {
    const res = await fetch(`${API_BASE}/api/medications`);
    medications = await res.json();
    return medications;
}

async function fetchAssignments() {
    const res = await fetch(`${API_BASE}/api/assignments`);
    assignments = await res.json();
    return assignments;
}

async function fetchLogs() {
    try {
        const response = await fetch(`${API_BASE}/api/logs`);
        if (response.ok) {
            logs = await response.json();
            return logs;
        }
        throw new Error('Error al cargar logs');
    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al cargar logs', 'error');
        return [];
    }
}

async function loadDashboardStats() {
    await Promise.all([
        fetchPatients(),
        fetchMedications(),
        fetchAssignments(),
        fetchLogs()
    ]);

    document.getElementById('totalPatients').textContent = patients.length;
    document.getElementById('totalMedications').textContent = medications.length;
    document.getElementById('totalAssignments').textContent = assignments.length;
    document.getElementById('totalLogs').textContent = logs.length;
}



async function loadMedications() {
    const medicationsData = await fetchMedications();
    const container = document.getElementById('medicationsContainer');
    if (medicationsData.length === 0) {
        container.innerHTML = '<p class="no-data">No hay medicamentos registrados</p>';
        return;
    }
    const limited = medicationsData.slice(0, 3);
    container.innerHTML = limited.map(medication => `
        <div class="card">
            <div class="card-header">
                <h4>${medication.name}</h4>
                <div class="card-actions">
                    <button onclick="editMedication(${medication.id})" class="btn btn-small btn-secondary">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button onclick="confirmDelete('medication', ${medication.id})" class="btn btn-small btn-danger">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            <div class="card-body">
                <p><strong>Dosis:</strong> ${medication.dose}</p>
                <p><strong>Frecuencia:</strong> Cada ${medication.frequencyHours} horas</p>
                <p><strong>Hora de inicio:</strong> ${medication.startTime}</p>
                <p><strong>Duración:</strong> ${medication.treatmentDurationDays} días</p>
                ${medication.instructions ? `<p><strong>Instrucciones:</strong> ${medication.instructions}</p>` : ''}
            </div>
        </div>
    `).join('');
}

async function loadAssignments() {
    const assignmentsData = await fetchAssignments();
    const container = document.getElementById('assignmentsContainer');
    if (assignmentsData.length === 0) {
        container.innerHTML = '<p class="no-data">No hay asignaciones registradas</p>';
        return;
    }
    const limited = assignmentsData.slice(0, 3);
    container.innerHTML = limited.map(assignment => {
        const patient = patients.find(p => p.id === assignment.patientId);
        const medication = medications.find(m => m.id === assignment.medicineId);
        return `
            <div class="card">
                <div class="card-header">
                    <h4>Asignación #${assignment.id}</h4>
                    <div class="card-actions">
                        <button onclick="editAssignment(${assignment.id})" class="btn btn-small btn-secondary">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button onclick="confirmDelete('assignment', ${assignment.id})" class="btn btn-small btn-danger">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <p><strong>Paciente:</strong> ${patient ? `${patient.firstName} ${patient.lastName}` : 'N/A'}</p>
                    <p><strong>Medicamento:</strong> ${medication ? medication.name : 'N/A'}</p>
                    <p><strong>Frecuencia:</strong> Cada ${assignment.frequencyMinutes} minutos</p>
                    <p><strong>Inicio:</strong> ${formatDate(assignment.startTime)}</p>
                </div>
            </div>
        `;
    }).join('');
}
const logsPerPage = 5;
async function loadLogs(page = 1) {
    // Actualiza la página actual
    currentLogPage = page;

    // Asegura que patients y medications estén cargados
    if (patients.length === 0) await fetchPatients();
    if (medications.length === 0) await fetchMedications();

    // Obtiene los logs
    const logsData = await fetchLogs();

    const container = document.getElementById('logsContainer');
    const pagination = document.getElementById('logsPagination');

    if (!logsData || logsData.length === 0) {
        container.innerHTML = '<p class="no-data">No hay logs registrados</p>';
        pagination.innerHTML = '';
        return;
    }

    // Calcula total de páginas
    const totalPages = Math.ceil(logsData.length / logsPerPage);

    // Limita la página a valores válidos
    if (page < 1) page = 1;
    if (page > totalPages) page = totalPages;

    // Define rango de logs para la página actual
    const start = (page - 1) * logsPerPage;
    const end = start + logsPerPage;
    const logsToShow = logsData.slice(start, end);

    // Renderiza los logs
    container.innerHTML = logsToShow.map(log => {
        const patient = patients.find(p => p.id === log.patientId);
        const medication = medications.find(m => m.id === log.medicineId);
        return `
            <div class="card">
                <div class="card-header">
                    <h4>Log #${log.id}</h4>
                </div>
                <div class="card-body">
                    <p><strong>Paciente:</strong> ${patient ? `${patient.firstName} ${patient.lastName}` : 'N/A'}</p>
                    <p><strong>Medicamento:</strong> ${medication ? medication.name : 'N/A'}</p>
                    <p><strong>Enviado:</strong> ${formatDate(log.reminderSentAt)}</p>
                    <p><strong>Mensaje:</strong> ${log.message}</p>
                </div>
            </div>
        `;
    }).join('');

    // Renderiza botones de paginación
    pagination.innerHTML = '';

    // Botón "Anterior"
    pagination.innerHTML += `<button class="page-btn" ${page === 1 ? 'disabled' : ''} onclick="loadLogs(${page - 1})">Anterior</button>`;

    // Botones numerados
    for (let i = 1; i <= totalPages; i++) {
        pagination.innerHTML += `
            <button class="page-btn ${i === page ? 'active' : ''}" onclick="loadLogs(${i})">${i}</button>
        `;
    }

    // Botón "Siguiente"
    pagination.innerHTML += `<button class="page-btn" ${page === totalPages ? 'disabled' : ''} onclick="loadLogs(${page + 1})">Siguiente</button>`;
}



async function loadPatientsForSelect() {
    const select = document.getElementById('assignmentPatient');
    if (!select) return;
    await fetchPatients();
    select.innerHTML = '<option value="">Seleccionar paciente...</option>' +
        patients.map(p => `<option value="${p.id}">${p.firstName} ${p.lastName}</option>`).join('');
}

async function loadMedicationsForSelect() {
    const select = document.getElementById('assignmentMedicine');
    if (!select) return;
    await fetchMedications();
    select.innerHTML = '<option value="">Seleccionar medicamento...</option>' +
        medications.map(m => `<option value="${m.id}">${m.name}</option>`).join('');
}

// ================== CONFIRMACIÓN ==================

function confirmDelete(type, id) {
    const msg = `¿Está seguro de que desea eliminar este ${type === 'patient' ? 'paciente' : type === 'medication' ? 'medicamento' : 'asignación'}?`;
    if (confirm(msg)) {
        switch (type) {
            case 'patient': deletePatient(id); break;
            case 'medication': deleteMedication(id); break;
            case 'assignment': deleteAssignment(id); break;
        }
    }
}

// ================== MODAL ==================

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
    currentEditId = null;
    currentEditType = null;
}

window.onclick = function (event) {
    const modal = document.getElementById('editModal');
    if (event.target === modal) closeModal();
};

document.querySelector('.close').onclick = closeModal;

// ================== EVENTOS ==================

document.addEventListener('DOMContentLoaded', function () {
    loadDashboardStats();

    const patientForm = document.getElementById('patientForm');
    if (patientForm) {
        patientForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            const patientData = {
                firstName: document.getElementById('patientFirstName').value,
                lastName: document.getElementById('patientLastName').value,
                email: document.getElementById('patientEmail').value,
                phone: document.getElementById('patientPhone').value,
                status: document.getElementById('patientStatus').value,
                registrationDate: new Date().toISOString().split('T')[0]
            };
            const success = await createPatient(patientData);
            if (success) patientForm.reset();
        });
    }

    const medicationForm = document.getElementById('medicationForm');
    if (medicationForm) {
        medicationForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            const medicationData = {
                name: document.getElementById('medicationName').value,
                dose: document.getElementById('medicationDose').value,
                frequencyHours: parseInt(document.getElementById('medicationFrequencyHours').value),
                instructions: document.getElementById('medicationInstructions').value,
                startTime: document.getElementById('medicationStartTime').value,
                treatmentDurationDays: parseInt(document.getElementById('medicationTreatmentDays').value)
            };
            const success = await createMedication(medicationData);
            if (success) medicationForm.reset();
        });
    }

    const assignmentForm = document.getElementById('assignmentForm');
    if (assignmentForm) {
        assignmentForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            const assignmentData = {
                patientId: parseInt(document.getElementById('assignmentPatient').value),
                medicineId: parseInt(document.getElementById('assignmentMedicine').value),
                frequencyMinutes: parseInt(document.getElementById('assignmentFrequencyMinutes').value),
                startTime: document.getElementById('assignmentStartTime').value
            };
            const success = await createAssignment(assignmentData);
            if (success) assignmentForm.reset();
        });
    }
});
