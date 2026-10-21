const loginView = document.querySelector("#login-view");
const appView = document.querySelector("#app-view");
const loginForm = document.querySelector("#login-form");
const clientForm = document.querySelector("#client-form");
const logoutButton = document.querySelector("#logout-button");
const refreshButton = document.querySelector("#refresh-button");
const loginMessage = document.querySelector("#login-message");
const clientMessage = document.querySelector("#client-message");
const listMessage = document.querySelector("#list-message");
const clientList = document.querySelector("#client-list");
const sessionLabel = document.querySelector("#session-label");
let accessToken = sessionStorage.getItem("accessToken") || "";
let signedInEmail = sessionStorage.getItem("signedInEmail") || "";

function showMessage(element, message, error = false) {
  element.textContent = message || "";
  element.classList.toggle("error", error);
}
function showSignedOut() {
  accessToken = "";
  signedInEmail = "";
  sessionStorage.removeItem("accessToken");
  sessionStorage.removeItem("signedInEmail");
  loginView.classList.remove("hidden");
  appView.classList.add("hidden");
  logoutButton.classList.add("hidden");
  clientList.replaceChildren();
}
function showSignedIn() {
  loginView.classList.add("hidden");
  appView.classList.remove("hidden");
  logoutButton.classList.remove("hidden");
  sessionLabel.textContent = signedInEmail;
  loadClients();
}
async function apiRequest(path, options = {}) {
  const headers = new Headers(options.headers || {});
  headers.set("Accept", "application/json");
  if (options.body) headers.set("Content-Type", "application/json");
  if (accessToken) headers.set("Authorization", `Bearer ${accessToken}`);
  const response = await fetch(path, { ...options, headers });
  if (response.status === 401 && path !== "/api/auth/login") {
    showSignedOut();
    showMessage(loginMessage, "Sua sessão expirou. Entre novamente.", true);
    throw new Error("Sessão expirada.");
  }
  if (!response.ok) {
    let detail = "Não foi possível concluir a operação.";
    try { const body = await response.json(); detail = body.detail || body.message || detail; } catch { /* resposta sem JSON */ }
    throw new Error(detail);
  }
  if (response.status === 204) return null;
  return response.json();
}
loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  showMessage(loginMessage, "Validando acesso…");
  const form = new FormData(loginForm);
  try {
    const result = await apiRequest("/api/auth/login", { method: "POST", body: JSON.stringify({ email: form.get("email"), senha: form.get("senha") }) });
    accessToken = result.accessToken;
    signedInEmail = result.email;
    sessionStorage.setItem("accessToken", accessToken);
    sessionStorage.setItem("signedInEmail", signedInEmail);
    loginForm.reset();
    showMessage(loginMessage, "");
    showSignedIn();
  } catch (error) { showMessage(loginMessage, error.message || "Falha ao entrar.", true); }
});
logoutButton.addEventListener("click", showSignedOut);
refreshButton.addEventListener("click", loadClients);
clientForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  showMessage(clientMessage, "Salvando cadastro…");
  const form = new FormData(clientForm);
  const value = (name) => String(form.get(name) || "").trim();
  const payload = {
    nome: value("nome"), cpf: value("cpf"), email: value("email"), telefone: value("telefone"),
    dataNascimento: value("dataNascimento") || null, observacoesClinicas: value("observacoesClinicas") || null,
    enderecos: [{ logradouro: value("logradouro"), numero: value("numero"), complemento: value("complemento") || null,
      bairro: value("bairro"), cidade: value("cidade"), estado: value("estado").toUpperCase(), cep: value("cep") }]
  };
  try {
    await apiRequest("/api/clientes", { method: "POST", body: JSON.stringify(payload) });
    clientForm.reset();
    showMessage(clientMessage, "Paciente cadastrado com sucesso.");
    await loadClients();
  } catch (error) { showMessage(clientMessage, error.message || "Não foi possível cadastrar.", true); }
});
async function loadClients() {
  showMessage(listMessage, "Carregando cadastros…");
  try {
    const clients = await apiRequest("/api/clientes");
    clientList.replaceChildren();
    if (!clients.length) { showMessage(listMessage, "Ainda não há pacientes cadastrados."); return; }
    clients.forEach((client) => {
      const card = document.createElement("article"); card.className = "client-card";
      const heading = document.createElement("h3"); heading.textContent = client.nome;
      const email = document.createElement("p"); email.textContent = client.email;
      const phone = document.createElement("p"); phone.textContent = client.telefone;
      const address = document.createElement("p"); const first = client.enderecos?.[0];
      address.textContent = first ? `${first.logradouro}, ${first.numero} · ${first.cidade}/${first.estado}` : "Sem endereço";
      card.append(heading, email, phone, address); clientList.append(card);
    });
    showMessage(listMessage, `${clients.length} cadastro(s) encontrado(s).`);
  } catch (error) { if (accessToken) showMessage(listMessage, error.message || "Falha ao carregar cadastros.", true); }
}
if (accessToken) showSignedIn(); else showSignedOut();
