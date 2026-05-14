const BASE = '';

function show(data) {
    document.getElementById('result').textContent =
        typeof data === 'object' ? JSON.stringify(data, null, 2) : String(data);
}

async function request(method, url, body) {
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(BASE + url, opts);
    if (res.status === 204) return show('Operacion exitosa (sin contenido)');
    const text = await res.text();
    try { show(JSON.parse(text)); } catch { show(text); }
}

function createRoot() {
    const name = document.getElementById('rootName').value;
    const type = document.getElementById('rootType').value;
    request('POST', '/nodes/root', { name, type });
}

function addChild() {
    const parentId = document.getElementById('parentId').value;
    const name = document.getElementById('childName').value;
    const type = document.getElementById('childType').value;
    request('POST', `/nodes/${parentId}/children`, { name, type });
}

function findNode() {
    const id = document.getElementById('searchId').value;
    request('GET', `/nodes/${id}`);
}

function deleteNode() {
    const id = document.getElementById('deleteId').value;
    request('DELETE', `/nodes/${id}`);
}

function getTree() { request('GET', '/tree'); }
function getDFS() { request('GET', '/tree/traversal?type=DFS'); }
function getBFS() { request('GET', '/tree/traversal?type=BFS'); }
function getHeight() { request('GET', '/tree/height'); }
function validate() { request('GET', '/tree/validate'); }

function getSubtree() {
    const id = document.getElementById('nodeOpId').value;
    request('GET', `/tree/${id}`);
}

function getPath() {
    const id = document.getElementById('nodeOpId').value;
    request('GET', `/nodes/${id}/path`);
}

function getAncestors() {
    const id = document.getElementById('nodeOpId').value;
    request('GET', `/nodes/${id}/ancestors`);
}

function getDepth() {
    const id = document.getElementById('nodeOpId').value;
    request('GET', `/nodes/${id}/depth`);
}
