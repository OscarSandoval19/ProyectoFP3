const API_URL = "http://localhost:8080/nodes";

const treeContainer = document.getElementById("tree-container");
const nodeForm = document.getElementById("node-form");
const nameInput = document.getElementById("name");
const typeSelect = document.getElementById("type");
const parentSelect = document.getElementById("parentId");

let nodes = [];

async function fetchNodes() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("No se pudo obtener la lista de nodos");
        }

        nodes = await response.json();
        renderParentOptions();
        renderTree();
    } catch (error) {
        treeContainer.innerHTML = `<p class="empty">Error al cargar nodos. Verifique que el servidor esté encendido.</p>`;
    }
}

async function createNode(name, type, parentId) {
    const endpoint = parentId ? API_URL : `${API_URL}/root`;

    const body = parentId
        ? { name, type, parentId }
        : { name, type };

    const response = await fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    });

    if (!response.ok) {
        throw new Error("No se pudo crear el nodo");
    }
}

async function deleteNode(id) {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    });

    if (!response.ok) {
        throw new Error("No se pudo eliminar el nodo");
    }
}

function renderParentOptions() {
    parentSelect.innerHTML = `<option value="">Sin padre (raíz)</option>`;

    nodes
        .filter(node => node.type === "FOLDER")
        .forEach(node => {
            const option = document.createElement("option");
            option.value = node.id;
            option.textContent = `${node.name} (${node.type})`;
            parentSelect.appendChild(option);
        });
}

function renderTree() {
    treeContainer.innerHTML = "";

    if (nodes.length === 0) {
        treeContainer.innerHTML = `<p class="empty">No hay carpetas ni archivos creados.</p>`;
        return;
    }

    const roots = nodes.filter(node => node.parentId === null || node.parentId === "");

    roots.forEach(root => {
        treeContainer.appendChild(renderNode(root, 0));
    });
}

function renderNode(node, level) {
    const wrapper = document.createElement("div");

    const nodeElement = document.createElement("div");
    nodeElement.className = "node";
    nodeElement.style.marginLeft = `${level * 28}px`;

    const icon = node.type === "FOLDER" ? "📁" : "📄";

    nodeElement.innerHTML = `
        <div class="node-info">
            <span>${icon}</span>
            <span class="node-name">${node.name}</span>
            <span class="node-type">${node.type}</span>
        </div>
        <button class="delete-btn" data-id="${node.id}">Eliminar</button>
    `;

    const deleteButton = nodeElement.querySelector(".delete-btn");
    deleteButton.addEventListener("click", async () => {
        const confirmDelete = confirm(`¿Eliminar "${node.name}" y sus hijos?`);

        if (!confirmDelete) {
            return;
        }

        try {
            await deleteNode(node.id);
            await fetchNodes();
        } catch (error) {
            alert(error.message);
        }
    });

    wrapper.appendChild(nodeElement);

    const children = nodes.filter(child => child.parentId === node.id);

    children.forEach(child => {
        wrapper.appendChild(renderNode(child, level + 1));
    });

    return wrapper;
}

nodeForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const name = nameInput.value.trim();
    const type = typeSelect.value;
    const parentId = parentSelect.value;

    if (!name) {
        alert("Debe ingresar un nombre");
        return;
    }

    try {
        await createNode(name, type, parentId);
        nodeForm.reset();
        await fetchNodes();
    } catch (error) {
        alert(error.message);
    }
});

fetchNodes();