const carousel = document.querySelector(".clinic-carousel");

if (carousel) {
  const slides = [...carousel.querySelectorAll(".carousel-slide")];
  const indicators = [...carousel.querySelectorAll(".carousel-indicator")];
  const previousButton = carousel.querySelector("[data-carousel-previous]");
  const nextButton = carousel.querySelector("[data-carousel-next]");
  const toggleButton = carousel.querySelector("[data-carousel-toggle]");
  const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)");
  let activeIndex = 0;
  let isPlaying = !reducedMotion.matches;
  let timer = null;

  function showSlide(index) {
    activeIndex = (index + slides.length) % slides.length;
    slides.forEach((slide, slideIndex) => {
      const active = slideIndex === activeIndex;
      slide.classList.toggle("is-active", active);
      slide.setAttribute("aria-hidden", String(!active));
      slide.inert = !active;
    });
    indicators.forEach((indicator, indicatorIndex) => {
      const active = indicatorIndex === activeIndex;
      indicator.classList.toggle("is-active", active);
      if (active) indicator.setAttribute("aria-current", "true");
      else indicator.removeAttribute("aria-current");
    });
  }

  function stopTimer() {
    window.clearInterval(timer);
    timer = null;
  }

  function startTimer() {
    stopTimer();
    if (isPlaying && !carousel.matches(":hover") && !carousel.contains(document.activeElement) && !document.hidden) {
      timer = window.setInterval(() => showSlide(activeIndex + 1), 6500);
    }
  }

  function updateToggle() {
    toggleButton.setAttribute("aria-label", isPlaying ? "Pausar reprodução" : "Retomar reprodução");
    toggleButton.innerHTML = isPlaying ? 'Ⅱ <span>Pausar</span>' : '▶ <span>Reproduzir</span>';
  }

  previousButton.addEventListener("click", () => showSlide(activeIndex - 1));
  nextButton.addEventListener("click", () => showSlide(activeIndex + 1));
  indicators.forEach((indicator, index) => indicator.addEventListener("click", () => showSlide(index)));
  toggleButton.addEventListener("click", () => {
    isPlaying = !isPlaying;
    updateToggle();
    startTimer();
  });
  carousel.addEventListener("pointerenter", stopTimer);
  carousel.addEventListener("pointerleave", startTimer);
  carousel.addEventListener("focusin", stopTimer);
  carousel.addEventListener("focusout", () => window.setTimeout(startTimer, 0));
  document.addEventListener("visibilitychange", startTimer);
  reducedMotion.addEventListener?.("change", () => {
    if (reducedMotion.matches) {
      isPlaying = false;
      updateToggle();
    }
    startTimer();
  });

  updateToggle();
  showSlide(0);
  startTimer();
}
