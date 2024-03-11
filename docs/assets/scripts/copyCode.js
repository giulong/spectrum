const copyHeaders = document.querySelectorAll('.copy-header');
const copyButtons = document.querySelectorAll('.copy-button');

copyButtons.forEach((copyButton, index) => {
    try {
        const code = copyHeaders[index].parentElement.nextElementSibling.innerText;

        copyButton.addEventListener('click', () => {
            window.navigator.clipboard.writeText(code);
            const copyText = copyButton.querySelector('.copy-text');
            const copyIcon = copyButton.querySelector('.copy-icon');

            copyText.style.display = 'inline';
            copyIcon.src = 'assets/images/check.png';

            setTimeout(() => {
                copyText.style.display = 'none';
                copyIcon.src = 'assets/images/copy.png';
            }, 2000);
        });
    } catch (error) {
        console.error(error);
    }
});
