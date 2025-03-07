var videoPausedTimeoutId;

function frameClicked() {
    Array
        .from(document.querySelectorAll('div[data-frame]'))
        .map(div => div.closest('tr'))
        .forEach(tr => tr.onclick = () => {
            const dataFrameDiv = tr.querySelector('div[data-frame]');
            const frameNumber = parseInt(dataFrameDiv.dataset.frame);
            const video = tr.closest('table').querySelector('video');

            video.currentTime = frameNumber;
        });
}

window.addEventListener('load', (event) => {
    const testContent = document.querySelector('div.test-content');

    Array
        .from(document.querySelectorAll('video'))
        .map(video => video.closest('tr'))
        .forEach(tr => {
            tr.classList.add('video-wrapper');
            tr.style.backgroundColor = window.getComputedStyle(testContent).getPropertyValue('background-color');
        });

    const classListObserver = new MutationObserver((mutations) => mutations
        .filter(mutation => mutation.attributeName === 'class')
        .filter(mutation => mutation.target.classList.contains('active'))
        .forEach(mutation => frameClicked()));

    document
        .querySelectorAll('li.test-item')
        .forEach(li => classListObserver.observe(li, { attributes: true }));

    frameClicked();
});

function syncVideoWithStep(event) {
    clearTimeout(videoPausedTimeoutId);

    const video = event.target;
    const frameNumber = Math.floor(video.currentTime);

    if (frameNumber == video.duration) {
        return;
    }

    const videoTable = video.closest('table');
    const videoTr = video.closest('tr');

    const currentFrameDiv = videoTable.querySelector(`div[data-frame="${frameNumber}"]`);

    if (!currentFrameDiv) {
        return;
    }

    Array
        .from(videoTable.querySelectorAll('tr'))
        .filter(tr => tr != videoTr)
        .forEach(tr => {
            tr.classList.add('darkened');
            tr.classList.remove('highlighted', 'no-more-darkened');
        });

    const currentFrameTr = currentFrameDiv.closest('tr');
    const block = currentFrameTr.getBoundingClientRect().top < videoTr.getBoundingClientRect().height ? 'center' : 'nearest';

    currentFrameTr.scrollIntoView({ block: block });
    currentFrameTr.classList.remove('darkened');
    currentFrameTr.classList.add('highlighted');
}

function videoPaused(event) {
    const videoTable = event.target.closest('table');

    videoPausedTimeoutId = setTimeout(() => {
        Array
            .from(videoTable.querySelectorAll('tr'))
            .forEach(tr => {
                tr.classList.add('no-more-darkened');
                tr.classList.remove('highlighted', 'darkened');
            });
    }, 500);
}
