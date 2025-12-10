// Funcionalidad para el formulario de artículos
document.addEventListener('DOMContentLoaded', function() {
    const inventarioInicial = document.getElementById('inventarioInicial');
    const entradas = document.getElementById('entradas');
    const salidas = document.getElementById('salidas');
    const inventarioFinal = document.getElementById('inventarioFinal');
    const formulario = document.querySelector('form');

    function calcularInventarioFinal() {
        const inicial = parseFloat(inventarioInicial?.value) || 0;
        const ent = parseFloat(entradas?.value) || 0;
        const sal = parseFloat(salidas?.value) || 0;
        const final = inicial + ent - sal;

        if (inventarioFinal) {
            inventarioFinal.value = final;
        }
    }

    // Prevenir múltiples envíos del formulario
    if (formulario) {
        formulario.addEventListener('submit', function(e) {
            const botonSubmit = this.querySelector('button[type="submit"]');
            if (botonSubmit) {
                botonSubmit.disabled = true;
                botonSubmit.innerHTML = '<ion-icon name="hourglass-outline"></ion-icon> Guardando...';
            }
        });
    }

    // Event listeners para calcular inventario
    if (inventarioInicial) {
        inventarioInicial.addEventListener('input', calcularInventarioFinal);
    }
    if (entradas) {
        entradas.addEventListener('input', calcularInventarioFinal);
    }
    if (salidas) {
        salidas.addEventListener('input', calcularInventarioFinal);
    }

    // Calcular inicialmente
    calcularInventarioFinal();
});

// Funciones de confirmación y eliminación
document.addEventListener('DOMContentLoaded', function() {
    // Confirmación para eliminar
    const linksEliminar = document.querySelectorAll('a.btn-eliminar, button.btn-eliminar');
    linksEliminar.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!confirm('¿Está seguro de eliminar este artículo?')) {
                e.preventDefault();
                return false;
            }

            // Si es un enlace normal, dejar que proceda
            if (this.tagName === 'A') {
                const originalText = this.innerHTML;
                this.innerHTML = '<ion-icon name="hourglass-outline"></ion-icon> Eliminando...';
                this.style.pointerEvents = 'none';
            }
        });
    });

    // Prevenir múltiples clics en botones
    const botones = document.querySelectorAll('.btn-primary, .btn-danger, .btn-editar');
    botones.forEach(boton => {
        boton.addEventListener('click', function(e) {
            if (this.disabled) {
                e.preventDefault();
                return;
            }

            if (this.type === 'submit' || this.classList.contains('btn-danger') || this.classList.contains('btn-primary')) {
                this.disabled = true;
                setTimeout(() => {
                    this.disabled = false;
                }, 2000);
            }
        });
    });
});

// Funcionalidad para la navegación y UI
document.addEventListener('DOMContentLoaded', function() {
    // Activar elemento actual en el menú
    const currentPath = window.location.pathname;
    const menuLinks = document.querySelectorAll('.sideBar a');

    menuLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

    // Centrar automáticamente todas las tablas
    centrarTablas();
});

// Función para centrar tablas
function centrarTablas() {
    const tablas = document.querySelectorAll('table');
    tablas.forEach(tabla => {
        // Crear contenedor si no existe
        if (!tabla.parentElement.classList.contains('table-container')) {
            const contenedor = document.createElement('div');
            contenedor.className = 'table-container';
            tabla.parentNode.insertBefore(contenedor, tabla);
            contenedor.appendChild(tabla);
        }

        // Forzar centrado de todas las celdas
        const celdas = tabla.querySelectorAll('td, th');
        celdas.forEach(celda => {
            celda.style.textAlign = 'center';
            celda.style.verticalAlign = 'middle';
        });
    });
}

// Funcionalidad adicional para movimientos
document.addEventListener('DOMContentLoaded', function() {
    const formularioMovimiento = document.querySelector('form');

    if (formularioMovimiento) {
        formularioMovimiento.addEventListener('submit', function(e) {
            const cantidad = document.getElementById('cantidad');
            const tipoMovimiento = document.getElementById('tipoMovimiento');
            const articuloId = document.getElementById('articuloId');

            // Validaciones básicas
            if (articuloId && !articuloId.value) {
                alert('Por favor seleccione un artículo');
                e.preventDefault();
                return;
            }

            if (tipoMovimiento && !tipoMovimiento.value) {
                alert('Por favor seleccione el tipo de movimiento');
                e.preventDefault();
                return;
            }

            if (cantidad && (!cantidad.value || cantidad.value < 1)) {
                alert('Por favor ingrese una cantidad válida');
                e.preventDefault();
                return;
            }

            // Confirmación para salidas
            if (tipoMovimiento && tipoMovimiento.value === 'SALIDA') {
                if (!confirm('¿Está seguro de registrar una salida de inventario?')) {
                    e.preventDefault();
                    return;
                }
            }

            // Loading state
            const botonSubmit = this.querySelector('button[type="submit"]');
            if (botonSubmit) {
                botonSubmit.disabled = true;
                botonSubmit.innerHTML = '<ion-icon name="hourglass-outline"></ion-icon> Procesando...';
            }
        });
    }
});

// Función para mostrar mensajes (si usas AJAX)
function mostrarMensaje(mensaje, tipo) {
    const divMensaje = document.createElement('div');
    divMensaje.className = tipo === 'exito' ? 'mensaje-exito' : 'mensaje-error';
    divMensaje.innerHTML = `
        <ion-icon name="${tipo === 'exito' ? 'checkmark-circle' : 'alert-circle'}"></ion-icon>
        ${mensaje}
    `;

    const contenido = document.querySelector('.contenido');
    contenido.insertBefore(divMensaje, contenido.firstChild);

    setTimeout(() => {
        divMensaje.remove();
    }, 5000);
}

document.addEventListener('DOMContentLoaded', function() {
    console.log('Sistema de Inventario cargado');

    // 1. Activar menú actual
    activarMenuActual();

    // 2. Configurar confirmación para eliminaciones
    configurarConfirmaciones();

    // 3. Configurar cálculo automático de inventario
    configurarCalculoInventario();

    // 4. Configurar validación de formularios
    configurarValidaciones();

    // 5. Centrar tablas
    centrarTablas();
});

/**
 * Activa el elemento del menú que corresponde a la página actual
 */
function activarMenuActual() {
    const currentPath = window.location.pathname;
    const menuLinks = document.querySelectorAll('.sideBar a');

    menuLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath ||
            (href !== '/' && currentPath.startsWith(href))) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

/**
 * Configura confirmaciones para acciones de eliminar
 */
function configurarConfirmaciones() {
    const elementosEliminar = document.querySelectorAll('.btn-eliminar, a[href*="eliminar"]');

    elementosEliminar.forEach(elemento => {
        elemento.addEventListener('click', function(e) {
            const mensaje = this.getAttribute('data-confirm') ||
                           '¿Está seguro de realizar esta acción?';

            if (!confirm(mensaje)) {
                e.preventDefault();
                return false;
            }

            // Mostrar estado de carga
            if (this.tagName === 'BUTTON' || this.classList.contains('btn')) {
                this.classList.add('btn-loading');
                this.disabled = true;
            }
        });
    });
}

/**
 * Configura el cálculo automático de inventario final
 */
function configurarCalculoInventario() {
    const inventarioInicial = document.getElementById('inventarioInicial');
    const entradas = document.getElementById('entradas');
    const salidas = document.getElementById('salidas');
    const inventarioFinal = document.getElementById('inventarioFinal');

    if (!inventarioInicial || !inventarioFinal) return;

    function calcular() {
        const inicial = parseInt(inventarioInicial.value) || 0;
        const ent = parseInt(entradas ? entradas.value : 0) || 0;
        const sal = parseInt(salidas ? salidas.value : 0) || 0;
        const final = inicial + ent - sal;

        inventarioFinal.value = final;
    }

    // Escuchar cambios en los inputs
    inventarioInicial.addEventListener('input', calcular);
    if (entradas) entradas.addEventListener('input', calcular);
    if (salidas) salidas.addEventListener('input', calcular);

    // Calcular valor inicial
    calcular();
}

/**
 * Configura validaciones básicas de formularios
 */
function configurarValidaciones() {
    const formularios = document.querySelectorAll('form');

    formularios.forEach(formulario => {
        formulario.addEventListener('submit', function(e) {
            const botonSubmit = this.querySelector('button[type="submit"], input[type="submit"]');

            if (botonSubmit) {
                // Prevenir múltiples envíos
                if (botonSubmit.disabled) {
                    e.preventDefault();
                    return false;
                }

                botonSubmit.disabled = true;
                botonSubmit.classList.add('btn-loading');

                // Restaurar después de 5 segundos por si hay error
                setTimeout(() => {
                    botonSubmit.disabled = false;
                    botonSubmit.classList.remove('btn-loading');
                }, 5000);
            }

            // Validación específica para movimientos
            if (this.action.includes('/movimientos/registrar')) {
                const cantidad = document.getElementById('cantidad');
                const tipoMovimiento = document.getElementById('tipoMovimiento');

                if (cantidad && (!cantidad.value || cantidad.value < 1)) {
                    alert('Por favor ingrese una cantidad válida');
                    e.preventDefault();
                    botonSubmit.disabled = false;
                    botonSubmit.classList.remove('btn-loading');
                    return false;
                }

                if (tipoMovimiento && tipoMovimiento.value === 'SALIDA') {
                    if (!confirm('¿Confirmar salida de inventario?')) {
                        e.preventDefault();
                        botonSubmit.disabled = false;
                        botonSubmit.classList.remove('btn-loading');
                        return false;
                    }
                }
            }
        });
    });
}


function centrarTablas() {
    const tablas = document.querySelectorAll('table');

    tablas.forEach(tabla => {
        const celdas = tabla.querySelectorAll('td, th');
        celdas.forEach(celda => {
            celda.style.textAlign = 'center';
            celda.style.verticalAlign = 'middle';
        });
    });
}

function mostrarMensaje(mensaje, tipo = 'exito') {
    const contenedor = document.querySelector('.contenido');
    if (!contenedor) return;

    const divMensaje = document.createElement('div');
    divMensaje.className = tipo === 'exito' ? 'mensaje-exito' : 'mensaje-error';
    divMensaje.innerHTML = `
        <ion-icon name="${tipo === 'exito' ? 'checkmark-circle' : 'alert-circle'}"></ion-icon>
        <span>${mensaje}</span>
    `;

    contenedor.insertBefore(divMensaje, contenedor.firstChild);


    setTimeout(() => {
        divMensaje.remove();
    }, 5000);
}


function formatearMoneda(valor) {
    return new Intl.NumberFormat('es-PE', {
        style: 'currency',
        currency: 'PEN'
    }).format(valor);
}


function exportarExcel() {
    // Esta función se puede llamar desde los botones de exportación
    console.log('Exportando a Excel...');
    // La lógica real de exportación está en el backend
}

// Agregar al final de tu archivo JS existente

/**
 * Funciones para paginación y filtrado
 */
document.addEventListener('DOMContentLoaded', function() {
    // Configurar formularios de filtro
    configurarFiltros();

    // Configurar selects de tamaño de página
    configurarSelectsTamanio();
});

function configurarFiltros() {
    const formulariosFiltro = document.querySelectorAll('.filtro-form');

    formulariosFiltro.forEach(form => {
        form.addEventListener('submit', function(e) {
            const inputFiltro = this.querySelector('input[name="filtro"]');

            // Si el filtro está vacío, remover el parámetro de la URL
            if (inputFiltro && inputFiltro.value.trim() === '') {
                const url = new URL(window.location.href);
                url.searchParams.delete('filtro');
                window.location.href = url.toString();
                e.preventDefault();
            }
        });
    });
}

function configurarSelectsTamanio() {
    const selects = document.querySelectorAll('.select-tamanio');

    selects.forEach(select => {
        select.addEventListener('change', function() {
            cambiarTamanioPagina(this);
        });
    });
}

function cambiarTamanioPagina(select) {
    const size = select.value;
    const url = new URL(window.location.href);

    // Actualizar parámetros
    url.searchParams.set('size', size);
    url.searchParams.set('page', '0'); // Volver a primera página

    // Redirigir
    window.location.href = url.toString();
}

/**
 * Función para confirmar eliminación con parámetros de paginación
 */
function confirmarEliminarConPaginacion(url) {
    if (confirm('¿Está seguro de eliminar este artículo?')) {
        // Agregar parámetros de paginación actuales
        const urlActual = new URL(window.location.href);
        const page = urlActual.searchParams.get('page') || '0';
        const size = urlActual.searchParams.get('size') || '10';
        const filtro = urlActual.searchParams.get('filtro') || '';

        const urlFinal = new URL(url, window.location.origin);
        urlFinal.searchParams.set('page', page);
        urlFinal.searchParams.set('size', size);
        if (filtro) {
            urlFinal.searchParams.set('filtro', filtro);
        }

        window.location.href = urlFinal.toString();
        return false;
    }
    return false;
}

// Funciones para filtros mejorados
function inicializarFiltros() {
    // Mejorar placeholder de inputs de filtro
    const inputsFiltro = document.querySelectorAll('input[name="filtro"]');
    inputsFiltro.forEach(input => {
        if (!input.parentElement.classList.contains('filtro-input')) {
            const wrapper = document.createElement('div');
            wrapper.className = 'filtro-input';
            input.parentNode.insertBefore(wrapper, input);
            wrapper.appendChild(input);
        }
    });

    // Agregar estadísticas de filtro
    agregarEstadisticasFiltro();
}

function agregarEstadisticasFiltro() {
    const tablas = document.querySelectorAll('table');
    tablas.forEach(tabla => {
        const totalFilas = tabla.querySelectorAll('tbody tr').length;
        const cards = document.querySelectorAll('.card');

        cards.forEach(card => {
            const filtros = card.querySelectorAll('.filtro-form');
            if (filtros.length > 0) {
                const statsDiv = document.createElement('div');
                statsDiv.className = 'filtro-stats';
                statsDiv.innerHTML = `
                    <ion-icon name="information-circle-outline"></ion-icon>
                    <span>Mostrando <strong>${totalFilas}</strong> resultados</span>
                `;

                const filtroForm = filtros[0];
                if (!filtroForm.nextElementSibling?.classList.contains('filtro-stats')) {
                    filtroForm.parentNode.insertBefore(statsDiv, filtroForm.nextSibling);
                }
            }
        });
    });
}

// Funciones para paginación mejorada
function inicializarPaginacionMejorada() {
    // Reemplazar paginación existente con mejorada
    const paginaciones = document.querySelectorAll('.paginacion');

    paginaciones.forEach(paginacion => {
        if (!paginacion.classList.contains('mejorada')) {
            paginacion.classList.add('mejorada');

            const infoDiv = paginacion.querySelector('div > div:first-child');
            const controlsDiv = paginacion.querySelector('div > div:nth-child(2)');
            const selectDiv = paginacion.querySelector('div > div:last-child');

            if (infoDiv && controlsDiv && selectDiv) {
                const nuevaPaginacion = document.createElement('div');
                nuevaPaginacion.className = 'paginacion-mejorada';
                nuevaPaginacion.innerHTML = `
                    <div class="paginacion-header">
                        <div class="paginacion-info">
                            <span class="filtro-badge">
                                <ion-icon name="cube-outline"></ion-icon>
                                ${infoDiv.textContent.trim()}
                            </span>
                        </div>
                        <div class="pagina-actual-indicator">
                            <ion-icon name="bookmark-outline"></ion-icon>
                            Página ${parseInt(document.querySelector('.btn-paginacion.active')?.textContent || '1')}
                        </div>
                    </div>
                    <div class="paginacion-controls">
                        ${controlsDiv.innerHTML.replace(/btn-paginacion/g, 'btn-paginacion-mejorado')}
                    </div>
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 20px;">
                        <div></div>
                        <div class="paginacion-select">
                            <span>Mostrar:</span>
                            ${selectDiv.innerHTML.replace(/select-tamanio/g, 'paginacion-select-control')}
                        </div>
                    </div>
                `;

                paginacion.parentNode.replaceChild(nuevaPaginacion, paginacion);
            }
        }
    });
}

// Actualizar el evento DOMContentLoaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('Sistema de Inventario cargado - Versión Mejorada');

    // 1. Activar menú actual
    activarMenuActual();

    // 2. Configurar confirmación para eliminaciones
    configurarConfirmaciones();

    // 3. Configurar cálculo automático de inventario
    configurarCalculoInventario();

    // 4. Configurar validación de formularios
    configurarValidaciones();

    // 5. Centrar tablas
    centrarTablas();

    // 6. Inicializar filtros mejorados
    inicializarFiltros();

    // 7. Inicializar paginación mejorada
    inicializarPaginacionMejorada();

    // 8. Configurar botón de actualizar estadísticas
    configurarActualizarEstadisticas();
});

// Función para configurar el botón de actualizar estadísticas
function configurarActualizarEstadisticas() {
    const btnActualizar = document.querySelector('button[onclick="actualizarEstadisticas()"]');
    if (btnActualizar) {
        btnActualizar.onclick = function(e) {
            actualizarEstadisticasReales(this);
        };
    }
}

// Función mejorada para actualizar estadísticas
async function actualizarEstadisticasReales(boton) {
    const originalHTML = boton.innerHTML;

    try {
        boton.innerHTML = '<ion-icon name="hourglass-outline"></ion-icon> Actualizando...';
        boton.disabled = true;

        // Hacer petición AJAX al backend
        const response = await fetch('/reportes/estadisticas');
        const data = await response.json();

        if (data.success) {
            // Actualizar las estadísticas en la página
            actualizarEstadisticasEnPagina(data);

            // Actualizar fecha
            document.getElementById('fechaActual').textContent =
                new Date().toLocaleDateString('es-ES', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit'
                });

            // Mostrar mensaje de éxito
            mostrarMensaje('Estadísticas actualizadas correctamente', 'exito');
        } else {
            mostrarMensaje('Error al obtener estadísticas: ' + data.error, 'error');
        }

    } catch (error) {
        console.error('Error:', error);
        mostrarMensaje('Error de conexión al actualizar estadísticas', 'error');
    } finally {
        boton.innerHTML = originalHTML;
        boton.disabled = false;
    }
}

function actualizarEstadisticasEnPagina(data) {
    // Buscar y actualizar los elementos de estadísticas
    const elementosEstadisticas = document.querySelectorAll('.report-option p[style*="font-size: 24px"]');

    // Este es un ejemplo - necesitas ajustar los selectores según tu HTML
    // Normalmente actualizarías elementos específicos con IDs
    console.log('Estadísticas recibidas:', data);

    // Ejemplo de cómo actualizaría los valores
    const statsContainers = document.querySelectorAll('.report-option');
    statsContainers.forEach((container, index) => {
        const statElement = container.querySelector('p[style*="font-size: 24px"]');
        if (statElement) {
            switch(index) {
                case 0: // Total artículos
                    statElement.textContent = data.totalArticulos || '--';
                    break;
                case 1: // En stock
                    statElement.textContent = data.enStock || '--';
                    break;
                case 2: // Stock bajo
                    statElement.textContent = data.stockBajo || '--';
                    break;
                case 3: // Sin stock
                    statElement.textContent = data.sinStock || '--';
                    break;
            }
        }
    });
}