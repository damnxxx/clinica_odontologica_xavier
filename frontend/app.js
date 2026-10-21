// Protótipo estático. Nenhuma chamada à API ou armazenamento de dados é realizada.
document.querySelectorAll('a[href^="#"]').forEach((link) => {
  link.addEventListener('click', (event) => {
    const target = link.getAttribute('href');
    if (target !== '#inicio' && target !== '#') {
      event.preventDefault();
      const label = link.textContent.trim();
      window.alert(`${label.replace(/em breve/i, '').trim()}: módulo ainda não disponível neste protótipo.`);
    }
  });
});
