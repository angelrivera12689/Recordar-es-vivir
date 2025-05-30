class NotificationToggleManager {
    constructor() {
        this.baseUrl = 'http://localhost:8080/api/config';

        this.toggle = document.getElementById('notificationToggle');
        this.statusText = document.getElementById('statusText');
        this.statusContainer = document.getElementById('toggleStatus');
        this.notificationIcon = document.getElementById('notificationIcon');
        this.alertContainer = document.getElementById('alertContainer');
        this.loadingContainer = document.getElementById('loadingContainer');
        
        this.init();
    }

    async init() {
        this.showLoading(true);
        await this.loadCurrentStatus();

        this.toggle.addEventListener('change', (e) => {
            this.handleToggleChange(e.target.checked);
        });

        this.showLoading(false);
    }

    showLoading(show) {
        this.loadingContainer.style.display = show ? 'flex' : 'none';
    }

    async loadCurrentStatus() {
        try {
            const url = `${this.baseUrl}/email-status`;
            const response = await fetch(url);

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            const data = await response.json();
            const isEnabled = !!data.enabled; // asegurar boolean

            this.updateUI(isEnabled);

        } catch (error) {
            console.error('Error loading notification status:', error);
            this.showAlert('Error al cargar el estado de las notificaciones', 'error');
            this.updateUI(false);
        }
    }

    async handleToggleChange(enabled) {
        try {
            this.toggle.disabled = true;
            this.showLoading(true);

            const url = `${this.baseUrl}/toggle-email`;

            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ enabled })
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            const result = await response.json();

            this.updateUI(enabled);
            this.showAlert(result.message || `Notificaciones ${enabled ? 'activadas' : 'desactivadas'} correctamente`, 'success');

        } catch (error) {
            console.error('Error toggling notifications:', error);

            this.toggle.checked = !enabled;
            this.updateUI(!enabled);
            this.showAlert('Error al cambiar el estado. Intenta de nuevo.', 'error');

        } finally {
            this.toggle.disabled = false;
            this.showLoading(false);
        }
    }

    updateUI(enabled) {
        this.toggle.checked = enabled;
        this.statusText.textContent = enabled ? 'Activo' : 'Inactivo';
        this.statusContainer.className = `toggle-status ${enabled ? 'status-active' : 'status-inactive'}`;

        const iconElement = this.notificationIcon.querySelector('i');
        if (enabled) {
            iconElement.className = 'fas fa-bell';
            this.notificationIcon.classList.add('active');
        } else {
            iconElement.className = 'fas fa-bell-slash';
            this.notificationIcon.classList.remove('active');
        }
    }

    showAlert(message, type = 'success') {
        this.alertContainer.innerHTML = '';

        const alert = document.createElement('div');
        alert.className = `alert alert-${type}`;
        alert.textContent = message;

        this.alertContainer.appendChild(alert);

        setTimeout(() => {
            if (alert.parentNode) alert.remove();
        }, 5000);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new NotificationToggleManager();
});
