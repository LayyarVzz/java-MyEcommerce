(() => {
    const dialogSelector = '[data-confirm-title], [data-confirm-message]';
    let modalInstance = null;
    let modalElement = null;
    let acceptButton = null;
    let cancelButton = null;
    let titleElement = null;
    let messageElement = null;
    let iconElement = null;
    let pendingAction = null;

    const variantConfig = {
        danger: {
            icon: 'bi-exclamation-octagon-fill',
            buttonClass: 'btn-danger'
        },
        warning: {
            icon: 'bi-exclamation-triangle-fill',
            buttonClass: 'btn-warning text-dark'
        },
        primary: {
            icon: 'bi-info-circle-fill',
            buttonClass: 'btn-primary'
        }
    };

    const ensureDialog = () => {
        if (modalElement) {
            return true;
        }
        if (!window.bootstrap || !window.bootstrap.Modal) {
            return false;
        }

        modalElement = document.createElement('div');
        modalElement.className = 'modal fade platform-confirm-modal';
        modalElement.tabIndex = -1;
        modalElement.setAttribute('aria-hidden', 'true');
        modalElement.innerHTML = `
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="platform-confirm-shell">
                        <div class="platform-confirm-head">
                            <div class="platform-confirm-icon">
                                <i class="bi bi-exclamation-triangle-fill" aria-hidden="true"></i>
                            </div>
                            <div>
                                <h2 class="platform-confirm-title">请确认操作</h2>
                                <p class="platform-confirm-message">该操作需要确认后继续。</p>
                            </div>
                        </div>
                        <div class="platform-confirm-actions">
                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">再想想</button>
                            <button type="button" class="btn btn-danger" data-platform-confirm-accept>确认</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(modalElement);

        modalInstance = new window.bootstrap.Modal(modalElement);
        acceptButton = modalElement.querySelector('[data-platform-confirm-accept]');
        cancelButton = modalElement.querySelector('[data-bs-dismiss="modal"]');
        titleElement = modalElement.querySelector('.platform-confirm-title');
        messageElement = modalElement.querySelector('.platform-confirm-message');
        iconElement = modalElement.querySelector('.platform-confirm-icon i');

        acceptButton.addEventListener('click', () => {
            const action = pendingAction;
            pendingAction = null;
            modalInstance.hide();
            if (typeof action === 'function') {
                action();
            }
        });
        modalElement.addEventListener('hidden.bs.modal', () => {
            pendingAction = null;
        });

        return true;
    };

    const openPlatformDialog = (source, onAccept) => {
        if (!ensureDialog()) {
            onAccept();
            return;
        }

        const variant = variantConfig[source.dataset.confirmVariant] || variantConfig.danger;
        titleElement.textContent = source.dataset.confirmTitle || '请确认操作';
        messageElement.textContent = source.dataset.confirmMessage || '该操作需要确认后继续。';
        acceptButton.textContent = source.dataset.confirmText || '确认';
        cancelButton.textContent = source.dataset.confirmCancelText || '再想想';
        acceptButton.className = `btn ${variant.buttonClass}`;
        iconElement.className = `bi ${variant.icon}`;
        pendingAction = onAccept;
        modalInstance.show();
    };

    document.addEventListener('submit', (event) => {
        const form = event.target;
        if (!(form instanceof HTMLFormElement) || !form.matches(dialogSelector)) {
            return;
        }
        if (form.dataset.confirmResolved === 'true') {
            delete form.dataset.confirmResolved;
            return;
        }

        event.preventDefault();
        const submitter = event.submitter;
        openPlatformDialog(form, () => {
            form.dataset.confirmResolved = 'true';
            if (form.requestSubmit) {
                if (submitter && submitter.form === form) {
                    form.requestSubmit(submitter);
                } else {
                    form.requestSubmit();
                }
            } else {
                form.submit();
            }
        });
    });

    document.addEventListener('click', (event) => {
        const link = event.target.closest('a[data-confirm-title], a[data-confirm-message]');
        if (!link) {
            return;
        }

        event.preventDefault();
        openPlatformDialog(link, () => {
            window.location.href = link.href;
        });
    });
})();
