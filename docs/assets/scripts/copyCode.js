const codeSnippets = document.querySelectorAll('div.highlighter-rouge')
const buttonSnippet = `<span class="copy-header">
                         <button class="copy-button">
                             <span class="copy-text" style="display: none;">Copied!</span>
                             <img class="copy-icon" src="assets/images/copy.png" alt="copy"/>
                         </button>
                     </span>`;

(async () => {
    const response = await fetch('https://api.github.com/repos/giulong/spectrum/contents/docs/json-schemas');
    const json = await response.json();

    codeSnippets.forEach(codeSnippet => {
        const wrapper = document.createElement('span');
        wrapper.innerHTML = buttonSnippet;
        codeSnippet.prepend(wrapper);
    });

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
})()
