const form=document.querySelector('#booking-form');
const message=document.querySelector('#booking-message');
const timeSelect=form.elements.inicio;
const submitButton=document.querySelector('#submit-booking');
const getJson=async(url)=>{const response=await fetch(url);const body=await response.json();if(!response.ok)throw new Error(body.detail||'Não foi possível concluir a operação.');return body};
const options=async(path,select,render)=>{const items=await getJson(path);select.replaceChildren(new Option('Selecione',''),...items.map(item=>new Option(render(item),item.id)));};
Promise.all([options('/api/publico/profissionais',form.elements.profissionalId,p=>`${p.nome} · ${p.especialidade}`),options('/api/publico/servicos',form.elements.servicoId,s=>`${s.nome} (${s.duracaoMinutos} min)`)]).catch(()=>{message.textContent='Não foi possível carregar profissionais e serviços.';message.classList.add('error')});
const today=new Date(Date.now()-new Date().getTimezoneOffset()*60000).toISOString().slice(0,10);form.elements.data.min=today;
function clearSlots(){timeSelect.replaceChildren(new Option('Busque os horários primeiro',''));timeSelect.disabled=true;submitButton.disabled=true;}
form.querySelectorAll('select[name="profissionalId"],select[name="servicoId"],input[name="data"]').forEach(field=>field.addEventListener('change',clearSlots));
document.querySelector('#find-public-slots').addEventListener('click',async()=>{
  if(!form.elements.profissionalId.value||!form.elements.servicoId.value||!form.elements.data.value){message.textContent='Escolha profissional, serviço e data para consultar os horários.';message.classList.add('error');return;}
  message.textContent='Buscando horários livres...';message.classList.remove('error');clearSlots();
  try{
    const params=new URLSearchParams({profissionalId:form.elements.profissionalId.value,servicoId:form.elements.servicoId.value,de:form.elements.data.value,ate:form.elements.data.value});
    const slots=await getJson(`/api/publico/horarios?${params}`);
    if(!slots.length){message.textContent='Não há horários livres nessa data. Escolha outra data ou fale com a equipe.';return;}
    timeSelect.replaceChildren(new Option('Selecione um horário', ''),...slots.map(slot=>new Option(`${new Date(slot.inicio).toLocaleTimeString('pt-BR',{hour:'2-digit',minute:'2-digit'})} – ${new Date(slot.fim).toLocaleTimeString('pt-BR',{hour:'2-digit',minute:'2-digit'})}`,slot.inicio)));
    timeSelect.disabled=false;message.textContent=`${slots.length} horário(s) disponível(is). Selecione um para enviar sua solicitação.`;
  }catch(error){message.textContent=error.message;message.classList.add('error');}
});
timeSelect.addEventListener('change',()=>{submitButton.disabled=!timeSelect.value;});
form.addEventListener('submit',async event=>{
  event.preventDefault();if(!timeSelect.value)return;
  message.textContent='Enviando solicitação...';message.classList.remove('error');
  const data=Object.fromEntries(new FormData(form));const payload={pacienteNome:data.pacienteNome,pacienteEmail:data.pacienteEmail,pacienteTelefone:data.pacienteTelefone,profissionalId:data.profissionalId,servicoId:data.servicoId,inicio:data.inicio,observacao:data.observacao||null};
  try{const response=await fetch('/api/publico/agendamentos',{method:'POST',headers:{'Content-Type':'application/json','Accept':'application/json'},body:JSON.stringify(payload)});const result=await response.json();if(!response.ok)throw new Error(result.detail||'Não foi possível enviar sua solicitação.');form.reset();clearSlots();message.textContent=result.mensagem;}
  catch(error){message.textContent=error.message;message.classList.add('error');}
});
