const copyHeaders = document.querySelectorAll('.copy-header');
const copyButtons = document.querySelectorAll('.copy-button');

copyButtons.forEach((copyButton, index) => {
  const code = copyHeaders[index].parentElement.nextElementSibling.innerText;

  copyButton.addEventListener('click', () => {
    window.navigator.clipboard.writeText(code);
    const copyText = copyButton.querySelectorAll('.copy-text')[0];
    copyText.style.display = 'inline';

    setTimeout(() => copyText.style.display = 'none', 2000);
  });
});
