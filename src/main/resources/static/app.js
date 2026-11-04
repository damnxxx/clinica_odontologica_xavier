const loginView = document.querySelector("#login-view");
const appView = document.querySelector("#app-view");
const loginForm = document.querySelector("#login-form");
const clientForm = document.querySelector("#client-form");
const clientEditForm = document.querySelector("#client-edit-form");
const addressForm = document.querySelector("#address-form");
const clientDialog = document.querySelector("#client-dialog");
const addressDialog = document.querySelector("#address-dialog");
const logoutButton = document.querySelector("#logout-button");
const refreshButton = document.querySelector("#refresh-button");
const addAddressButton = document.querySelector("#add-address-button");
const addEditAddressButton = document.querySelector("#add-edit-address-button");
const closeClientDialogButton = document.querySelector("#close-client-dialog");
const closeAddressDialogButton = document.querySelector("#close-address-dialog");
const loginMessage = document.querySelector("#login-message");
const clientMessage = document.querySelector("#client-message");
const editClientMessage = document.querySelector("#edit-client-message");
const addressMessage = document.querySelector("#address-message");
const listMessage = document.querySelector("#list-message");
const clientList = document.querySelector("#client-list");
const clientAddresses = document.querySelector("#client-addresses");
const editDialogTitle = document.querySelector("#edit-dialog-title");
const addressDialogTitle = document.querySelector("#address-dialog-title");
const sessionLabel = document.querySelector("#session-label");
const agendaForm = document.querySelector("#agenda-form");
const appointmentList = document.querySelector("#appointment-list");
const appointmentMessage = document.querySelector("#appointment-message");
const agendaFormMessage = document.querySelector("#agenda-form-message");
const professionalForm = document.querySelector("#professional-form");
const serviceForm = document.querySelector("#service-form");
const bookSlotForm = document.querySelector("#book-slot-form");
const slotList = document.querySelector("#available-slot-list");
const availabilityForm = document.querySelector("#availability-form");
const blockForm = document.querySelector("#block-form");
const availabilityList = document.querySelector("#availability-list");
const blockList = document.querySelector("#block-list");
let accessToken = sessionStorage.getItem("accessToken") || "";
let signedInEmail = sessionStorage.getItem("signedInEmail") || "";
let editingClient = null;
let selectedAvailableSlot = null;

const addressFields = [
  ["logradouro", "Logradouro", 120, true],
  ["numero", "Número", 20, true],
  ["complemento", "Complemento", 80, false],
  ["bairro", "Bairro", 80, true],
  ["cidade", "Cidade", 80, true],
  ["estado", "Estado (UF)", 2, true],
  ["cep", "CEP", 9, true]
];

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
  appointmentList?.replaceChildren();
}

function showSignedIn() {
  loginView.classList.add("hidden");
  appView.classList.remove("hidden");
  logoutButton.classList.remove("hidden");
  sessionLabel.textContent = signedInEmail;
  loadClients();
  loadAgenda();
}

async function loadAgendaOptions() {
  if (!agendaForm) return;
  const [professionals, services] = await Promise.all([
    fetch("/api/publico/profissionais").then((r) => r.json()),
    fetch("/api/publico/servicos").then((r) => r.json())
  ]);
  const fill = (select, rows, label) => select.replaceChildren(new Option("Selecione", ""), ...rows.map((row) => new Option(label(row), row.id)));
  document.querySelectorAll('select[name="profissionalId"]').forEach((select) => fill(select, professionals, (p) => `${p.nome} · ${p.especialidade}`));
  document.querySelectorAll('select[name="servicoId"]').forEach((select) => fill(select, services, (s) => `${s.nome} · ${s.duracaoMinutos} min`));
  const today = new Date();
  const localDate = (date) => new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0,10);
  agendaForm.elements.de.value = localDate(today);
  const lastDay = new Date(today); lastDay.setDate(lastDay.getDate()+14);
  agendaForm.elements.ate.value = localDate(lastDay);
  const now = new Date(Date.now()-today.getTimezoneOffset()*60000).toISOString().slice(0,16);
  blockForm.elements.inicio.min = now; blockForm.elements.fim.min = now;
}

function renderAvailableSlot(slot) {
  const item=document.createElement("article"); item.className="slot-card";
  addText(item,"strong",new Date(slot.inicio).toLocaleString("pt-BR"));
  addText(item,"p",`${slot.profissionalNome} · ${slot.servicoNome}`);
  const button=document.createElement("button"); button.type="button"; button.className="button secondary"; button.textContent="Selecionar paciente";
  button.addEventListener("click",()=>selectSlotForPatient(slot)); item.append(button); return item;
}

async function selectSlotForPatient(slot) {
  selectedAvailableSlot=slot;
  try {
    const patients=await apiRequest("/api/clientes");
    const select=bookSlotForm.elements.pacienteId;
    select.replaceChildren(new Option("Selecione o paciente cadastrado",""),...patients.map(p=>new Option(`${p.nome} · CPF ${p.cpf}`,p.id)));
    document.querySelector("#selected-slot-label").textContent=`${slot.profissionalNome} · ${slot.servicoNome} · ${new Date(slot.inicio).toLocaleString("pt-BR")}`;
    showMessage(document.querySelector("#book-slot-message"),"");
    bookSlotForm.classList.remove("hidden");
    bookSlotForm.scrollIntoView({behavior:"smooth",block:"center"});
  } catch(error) { showMessage(agendaFormMessage,error.message,true); }
}

async function loadSchedule(professionalId) {
  if(!professionalId||!availabilityList||!accessToken) return;
  try {
    const now=new Date(); const to=new Date(now); to.setDate(to.getDate()+60);
    const fromValue=new Date(now.getTime()-now.getTimezoneOffset()*60000).toISOString().slice(0,16)+":00";
    const toValue=new Date(to.getTime()-to.getTimezoneOffset()*60000).toISOString().slice(0,16)+":00";
    const [availability,blocks]=await Promise.all([
      apiRequest(`/api/profissionais/${professionalId}/disponibilidades`),
      apiRequest(`/api/profissionais/${professionalId}/bloqueios?de=${encodeURIComponent(fromValue)}&ate=${encodeURIComponent(toValue)}`)
    ]);
    availabilityList.replaceChildren();
    const days=["","Segunda-feira","Terça-feira","Quarta-feira","Quinta-feira","Sexta-feira","Sábado","Domingo"];
    availability.forEach(a=>{const row=document.createElement("div");row.className="schedule-item";addText(row,"span",`${days[a.diaSemana]} · ${a.inicio.slice(0,5)}–${a.fim.slice(0,5)} · intervalos de ${a.intervaloMinutos} min`);const remove=document.createElement("button");remove.className="button danger";remove.type="button";remove.textContent="Remover";remove.addEventListener("click",async()=>{await apiRequest(`/api/profissionais/${professionalId}/disponibilidades/${a.id}`,{method:"DELETE"});await loadSchedule(professionalId)});row.append(remove);availabilityList.append(row)});
    if(!availability.length) addText(availabilityList,"p","Nenhuma disponibilidade semanal cadastrada.");
    blockList.replaceChildren();
    blocks.forEach(b=>{const row=document.createElement("div");row.className="schedule-item";addText(row,"span",`${new Date(b.inicio).toLocaleString("pt-BR")} – ${new Date(b.fim).toLocaleString("pt-BR")}${b.motivo?` · ${b.motivo}`:""}`);const remove=document.createElement("button");remove.className="button danger";remove.type="button";remove.textContent="Remover";remove.addEventListener("click",async()=>{await apiRequest(`/api/profissionais/${professionalId}/bloqueios/${b.id}`,{method:"DELETE"});await loadSchedule(professionalId)});row.append(remove);blockList.append(row)});
    if(!blocks.length) addText(blockList,"p","Nenhum bloqueio nos próximos 60 dias.");
  } catch(error) { addText(availabilityList,"p",error.message); }
}

function renderAppointment(appointment) {
  const card = document.createElement("article"); card.className = "appointment-card";
  addText(card, "h3", `${new Date(appointment.inicio).toLocaleString("pt-BR")} · ${appointment.pacienteNome}`);
  addText(card, "p", `${appointment.servicoNome} com ${appointment.profissionalNome}`);
  addText(card, "p", `${appointment.pacienteTelefone} · ${appointment.pacienteEmail}`);
  if (appointment.observacao) addText(card, "p", appointment.observacao);
  const actions = document.createElement("div"); actions.className = "action-row";
  const status = document.createElement("select");
  [["SOLICITADO", "Solicitado"], ["CONFIRMADO", "Confirmado"], ["CANCELADO", "Cancelado"], ["CONCLUIDO", "Concluído"]].forEach(([value, label]) => status.add(new Option(label, value)));
  status.value = appointment.status; status.setAttribute("aria-label", `Status do agendamento de ${appointment.pacienteNome}`);
  status.addEventListener("change", async () => { try { await apiRequest(`/api/agendamentos/${appointment.id}`, { method: "PATCH", body: JSON.stringify({ status: status.value, pacienteId: appointment.pacienteId }) }); showMessage(appointmentMessage, "Status do agendamento atualizado."); await loadAgenda(); } catch (error) { status.value = appointment.status; showMessage(appointmentMessage, error.message, true); } });
  actions.append(status); card.append(actions); return card;
}

async function loadAgenda() {
  if (!appointmentList || !accessToken) return;
  try {
    const appointments = await apiRequest("/api/agendamentos"); appointmentList.replaceChildren();
    appointments.forEach((appointment) => appointmentList.append(renderAppointment(appointment)));
    showMessage(appointmentMessage, appointments.length ? `${appointments.length} agendamento(s).` : "Ainda não há agendamentos.");
  } catch (error) { showMessage(appointmentMessage, error.message, true); }
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
    try {
      const body = await response.json();
      detail = body.detail || body.message || detail;
    } catch { /* resposta sem JSON */ }
    throw new Error(detail);
  }
  if (response.status === 204) return null;
  return response.json();
}

function makeAddressEditor(address = {}) {
  const fieldset = document.createElement("fieldset");
  fieldset.className = "address-editor";

  const heading = document.createElement("div");
  heading.className = "address-editor-heading";
  const title = document.createElement("h3");
  title.className = "address-editor-title";
  const removeButton = document.createElement("button");
  removeButton.className = "button secondary remove-address-button";
  removeButton.type = "button";
  removeButton.textContent = "Remover endereço";
  removeButton.addEventListener("click", () => {
    const container = fieldset.parentElement;
    if (container.children.length <= 1) {
      showMessage(container === clientAddresses ? clientMessage : editClientMessage,
        "O cliente precisa manter pelo menos um endereço.", true);
      return;
    }
    fieldset.remove();
    updateAddressEditors(container);
  });
  heading.append(title, removeButton);
  fieldset.append(heading);

  const fields = document.createElement("div");
  fields.className = "address-fields";
  addressFields.forEach(([name, labelText, maxLength, required]) => {
    const label = document.createElement("label");
    label.append(document.createTextNode(labelText));
    const input = document.createElement("input");
    input.name = name;
    input.maxLength = maxLength;
    input.required = required;
    input.value = address[name] || "";
    if (name === "estado") input.minLength = 2;
    label.append(input);
    fields.append(label);
  });
  fieldset.append(fields);
  return fieldset;
}

function updateAddressEditors(container) {
  const entries = [...container.querySelectorAll(".address-editor")];
  entries.forEach((entry, index) => {
    entry.querySelector(".address-editor-title").textContent = `Endereço ${index + 1}`;
    entry.querySelector(".remove-address-button").disabled = entries.length <= 1;
  });
}

function appendAddressEditor(container, address = {}) {
  container.append(makeAddressEditor(address));
  updateAddressEditors(container);
}

function collectAddressEditors(container) {
  return [...container.querySelectorAll(".address-editor")].map((entry) => {
    const value = (name) => entry.querySelector(`[name="${name}"]`).value.trim();
    return {
      logradouro: value("logradouro"),
      numero: value("numero"),
      complemento: value("complemento") || null,
      bairro: value("bairro"),
      cidade: value("cidade"),
      estado: value("estado").toUpperCase(),
      cep: value("cep")
    };
  });
}

function readClientFields(form) {
  const formData = new FormData(form);
  const value = (name) => String(formData.get(name) || "").trim();
  return {
    nome: value("nome"),
    cpf: value("cpf"),
    email: value("email"),
    telefone: value("telefone"),
    dataNascimento: value("dataNascimento") || null,
    observacoesClinicas: value("observacoesClinicas") || null
  };
}

function readAddressFields(form) {
  const formData = new FormData(form);
  const value = (name) => String(formData.get(name) || "").trim();
  return {
    logradouro: value("logradouro"),
    numero: value("numero"),
    complemento: value("complemento") || null,
    bairro: value("bairro"),
    cidade: value("cidade"),
    estado: value("estado").toUpperCase(),
    cep: value("cep")
  };
}

function addText(parent, tag, text, className = "") {
  const element = document.createElement(tag);
  element.textContent = text;
  if (className) element.className = className;
  parent.append(element);
  return element;
}

function renderAddress(client, address, index) {
  const item = document.createElement("article");
  item.className = "address-card";
  const location = [address.logradouro, address.numero, address.complemento]
    .filter(Boolean).join(", ");
  addText(item, "h4", `Endereço ${index + 1}`);
  addText(item, "p", location);
  addText(item, "p", `${address.bairro} · ${address.cidade}/${address.estado} · CEP ${address.cep}`);

  const actions = document.createElement("div");
  actions.className = "action-row";
  const editButton = document.createElement("button");
  editButton.className = "button secondary";
  editButton.type = "button";
  editButton.textContent = "Alterar endereço";
  editButton.addEventListener("click", () => openAddressDialog(client, address));
  const deleteButton = document.createElement("button");
  deleteButton.className = "button danger";
  deleteButton.type = "button";
  deleteButton.textContent = "Excluir endereço";
  deleteButton.disabled = client.enderecos.length <= 1;
  deleteButton.title = deleteButton.disabled ? "O cliente precisa manter pelo menos um endereço." : "";
  deleteButton.addEventListener("click", () => deleteAddress(client, address));
  actions.append(editButton, deleteButton);
  item.append(actions);
  return item;
}

function renderClient(client) {
  const card = document.createElement("article");
  card.className = "client-card";
  const heading = document.createElement("div");
  heading.className = "client-card-heading";
  addText(heading, "h3", client.nome);
  const clientActions = document.createElement("div");
  clientActions.className = "action-row";

  const editButton = document.createElement("button");
  editButton.className = "button secondary";
  editButton.type = "button";
  editButton.textContent = "Alterar cliente";
  editButton.addEventListener("click", () => openClientDialog(client));
  const deleteButton = document.createElement("button");
  deleteButton.className = "button danger";
  deleteButton.type = "button";
  deleteButton.textContent = "Excluir cliente";
  deleteButton.addEventListener("click", () => deleteClient(client));
  clientActions.append(editButton, deleteButton);
  heading.append(clientActions);
  card.append(heading);

  addText(card, "p", `CPF: ${client.cpf}`);
  addText(card, "p", `E-mail: ${client.email}`);
  addText(card, "p", `Telefone: ${client.telefone}`);
  if (client.dataNascimento) addText(card, "p", `Nascimento: ${client.dataNascimento}`);

  const addressHeading = document.createElement("div");
  addressHeading.className = "address-list-heading";
  addText(addressHeading, "h4", `Endereços (${client.enderecos.length})`);
  const addButton = document.createElement("button");
  addButton.className = "button secondary";
  addButton.type = "button";
  addButton.textContent = "Adicionar endereço";
  addButton.addEventListener("click", () => openAddressDialog(client));
  addressHeading.append(addButton);
  card.append(addressHeading);

  const addresses = document.createElement("div");
  addresses.className = "address-list";
  client.enderecos.forEach((address, index) => addresses.append(renderAddress(client, address, index)));
  card.append(addresses);
  return card;
}

async function loadClients() {
  showMessage(listMessage, "Carregando cadastros...");
  try {
    const clients = await apiRequest("/api/clientes");
    clientList.replaceChildren();
    if (!clients.length) {
      showMessage(listMessage, "Ainda não há clientes cadastrados.");
      return;
    }
    clients.forEach((client) => clientList.append(renderClient(client)));
    showMessage(listMessage, `${clients.length} cliente(s) encontrado(s).`);
  } catch (error) {
    if (accessToken) showMessage(listMessage, error.message || "Falha ao carregar clientes.", true);
  }
}

function openClientDialog(client) {
  editingClient = client;
  editDialogTitle.textContent = `Alterar cliente: ${client.nome}`;
  ["nome", "cpf", "email", "telefone", "dataNascimento", "observacoesClinicas"].forEach((name) => {
    clientEditForm.elements.namedItem(name).value = client[name] || "";
  });
  showMessage(editClientMessage, "");
  clientDialog.showModal();
}

function openAddressDialog(client, address = null) {
  addressForm.reset();
  addressForm.dataset.clientId = client.id;
  addressForm.dataset.addressId = address?.id || "";
  addressDialogTitle.textContent = address ? "Alterar endereço" : `Adicionar endereço para ${client.nome}`;
  addressFields.forEach(([name]) => {
    addressForm.elements.namedItem(name).value = address?.[name] || "";
  });
  showMessage(addressMessage, "");
  addressDialog.showModal();
}

async function deleteClient(client) {
  if (!window.confirm(`Excluir o cliente ${client.nome} e todos os endereços?`)) return;
  try {
    await apiRequest(`/api/clientes/${client.id}`, { method: "DELETE" });
    showMessage(listMessage, "Cliente excluído.");
    await loadClients();
  } catch (error) {
    showMessage(listMessage, error.message || "Não foi possível excluir o cliente.", true);
  }
}

async function deleteAddress(client, address) {
  if (client.enderecos.length <= 1) {
    showMessage(listMessage, "O cliente precisa manter pelo menos um endereço.", true);
    return;
  }
  if (!window.confirm(`Excluir o endereço ${address.logradouro}, ${address.numero}?`)) return;
  try {
    await apiRequest(`/api/clientes/${client.id}/enderecos/${address.id}`, { method: "DELETE" });
    showMessage(listMessage, "Endereço excluído.");
    await loadClients();
  } catch (error) {
    showMessage(listMessage, error.message || "Não foi possível excluir o endereço.", true);
  }
}

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  showMessage(loginMessage, "Validando acesso...");
  const form = new FormData(loginForm);
  try {
    const result = await apiRequest("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ email: form.get("email"), senha: form.get("senha") })
    });
    accessToken = result.accessToken;
    signedInEmail = result.email;
    sessionStorage.setItem("accessToken", accessToken);
    sessionStorage.setItem("signedInEmail", signedInEmail);
    loginForm.reset();
    showMessage(loginMessage, "");
    showSignedIn();
  } catch (error) {
    showMessage(loginMessage, error.message || "Falha ao entrar.", true);
  }
});

clientForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  showMessage(clientMessage, "Salvando cadastro...");
  const payload = { ...readClientFields(clientForm), enderecos: collectAddressEditors(clientAddresses) };
  try {
    await apiRequest("/api/clientes", { method: "POST", body: JSON.stringify(payload) });
    clientForm.reset();
    clientAddresses.replaceChildren();
    appendAddressEditor(clientAddresses);
    showMessage(clientMessage, "Cliente cadastrado com sucesso.");
    await loadClients();
  } catch (error) {
    showMessage(clientMessage, error.message || "Não foi possível cadastrar o cliente.", true);
  }
});

clientEditForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!editingClient) return;
  showMessage(editClientMessage, "Salvando alterações...");
  const payload = {
    ...readClientFields(clientEditForm),
    enderecos: editingClient.enderecos.map(({ id, ...address }) => address)
  };
  try {
    await apiRequest(`/api/clientes/${editingClient.id}`, { method: "PUT", body: JSON.stringify(payload) });
    clientDialog.close();
    editingClient = null;
    await loadClients();
    showMessage(listMessage, "Dados do cliente alterados.");
  } catch (error) {
    showMessage(editClientMessage, error.message || "Não foi possível alterar o cliente.", true);
  }
});

addressForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const clientId = addressForm.dataset.clientId;
  const addressId = addressForm.dataset.addressId;
  const basePath = `/api/clientes/${clientId}/enderecos`;
  const path = addressId ? `${basePath}/${addressId}` : basePath;
  const method = addressId ? "PUT" : "POST";
  showMessage(addressMessage, "Salvando endereço...");
  try {
    await apiRequest(path, { method, body: JSON.stringify(readAddressFields(addressForm)) });
    addressDialog.close();
    await loadClients();
    showMessage(listMessage, addressId ? "Endereço alterado." : "Endereço adicionado.");
  } catch (error) {
    showMessage(addressMessage, error.message || "Não foi possível salvar o endereço.", true);
  }
});

addAddressButton.addEventListener("click", () => {
  appendAddressEditor(clientAddresses);
  showMessage(clientMessage, "");
});
refreshButton.addEventListener("click", loadClients);
document.querySelector("#agenda-refresh")?.addEventListener("click", loadAgenda);
agendaForm?.addEventListener("submit", async (event) => {
  event.preventDefault(); showMessage(agendaFormMessage, "Buscando horários livres..."); slotList.replaceChildren(); bookSlotForm.classList.add("hidden"); selectedAvailableSlot=null;
  try {
    const params=new URLSearchParams({profissionalId:agendaForm.elements.profissionalId.value,servicoId:agendaForm.elements.servicoId.value,de:agendaForm.elements.de.value,ate:agendaForm.elements.ate.value});
    const response=await fetch(`/api/publico/horarios?${params}`); const slots=await response.json();
    if(!response.ok) throw new Error(slots.detail||"Não foi possível buscar horários.");
    slots.forEach(slot=>slotList.append(renderAvailableSlot(slot)));
    showMessage(agendaFormMessage,slots.length?`${slots.length} horário(s) disponível(is).`:"Nenhum horário livre nesse período. Configure a disponibilidade do profissional ou escolha outras datas.");
  } catch(error) { showMessage(agendaFormMessage,error.message,true); }
});
bookSlotForm?.addEventListener("submit",async(event)=>{
  event.preventDefault(); if(!selectedAvailableSlot)return;
  showMessage(document.querySelector("#book-slot-message"),"Confirmando consulta...");
  const payload={pacienteId:bookSlotForm.elements.pacienteId.value,profissionalId:selectedAvailableSlot.profissionalId,servicoId:selectedAvailableSlot.servicoId,inicio:selectedAvailableSlot.inicio,observacao:bookSlotForm.elements.observacao.value.trim()||null};
  try { await apiRequest("/api/agendamentos",{method:"POST",body:JSON.stringify(payload)}); showMessage(document.querySelector("#book-slot-message"),"Consulta confirmada para o paciente cadastrado."); bookSlotForm.classList.add("hidden"); await loadAgenda(); agendaForm.requestSubmit(); }
  catch(error) { showMessage(document.querySelector("#book-slot-message"),error.message,true); }
});
document.querySelector("#cancel-slot-selection")?.addEventListener("click",()=>{selectedAvailableSlot=null;bookSlotForm.classList.add("hidden")});
availabilityForm?.addEventListener("submit",async(event)=>{
  event.preventDefault();const professionalId=availabilityForm.elements.profissionalId.value;const data=Object.fromEntries(new FormData(availabilityForm));data.diaSemana=Number(data.diaSemana);data.intervaloMinutos=Number(data.intervaloMinutos);
  try { await apiRequest(`/api/profissionais/${professionalId}/disponibilidades`,{method:"POST",body:JSON.stringify(data)});showMessage(document.querySelector("#availability-message"),"Disponibilidade semanal cadastrada.");await loadSchedule(professionalId); }
  catch(error) { showMessage(document.querySelector("#availability-message"),error.message,true); }
});
blockForm?.addEventListener("submit",async(event)=>{
  event.preventDefault();const professionalId=blockForm.elements.profissionalId.value;const data=Object.fromEntries(new FormData(blockForm));
  try { await apiRequest(`/api/profissionais/${professionalId}/bloqueios`,{method:"POST",body:JSON.stringify(data)});showMessage(document.querySelector("#block-message"),"Período bloqueado.");blockForm.reset();blockForm.elements.profissionalId.value=professionalId;await loadSchedule(professionalId); }
  catch(error) { showMessage(document.querySelector("#block-message"),error.message,true); }
});
document.querySelectorAll('#availability-form select[name="profissionalId"],#block-form select[name="profissionalId"]').forEach(select=>select.addEventListener("change",()=>loadSchedule(select.value)));
professionalForm?.addEventListener("submit", async (event) => {
  event.preventDefault(); const data = Object.fromEntries(new FormData(professionalForm)); data.experienciaAnos = Number(data.experienciaAnos);
  try { await apiRequest("/api/profissionais", { method: "POST", body: JSON.stringify(data) }); professionalForm.reset(); showMessage(document.querySelector("#professional-message"), "Profissional cadastrado."); await loadAgendaOptions(); }
  catch (error) { showMessage(document.querySelector("#professional-message"), error.message, true); }
});
serviceForm?.addEventListener("submit", async (event) => {
  event.preventDefault(); const data = Object.fromEntries(new FormData(serviceForm)); data.duracaoMinutos = Number(data.duracaoMinutos);
  try { await apiRequest("/api/servicos", { method: "POST", body: JSON.stringify(data) }); serviceForm.reset(); showMessage(document.querySelector("#service-message"), "Serviço cadastrado."); await loadAgendaOptions(); }
  catch (error) { showMessage(document.querySelector("#service-message"), error.message, true); }
});
logoutButton.addEventListener("click", showSignedOut);
closeClientDialogButton.addEventListener("click", () => clientDialog.close());
closeAddressDialogButton.addEventListener("click", () => addressDialog.close());

appendAddressEditor(clientAddresses);
loadAgendaOptions().catch(() => showMessage(agendaFormMessage, "Não foi possível carregar profissionais e serviços.", true));
if (accessToken) showSignedIn(); else showSignedOut();
