document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('[data-comment-form]');
    const commentsList = document.querySelector('[data-comments-list]');
    const messageBox = document.querySelector('[data-comment-message]');

    const showMessage = (message, success = true) => {
        if (!messageBox) {
            return;
        }
        messageBox.textContent = message;
        messageBox.className = `alert ${success ? 'alert-success' : 'alert-danger'} comment-alert`;
    };

    const updateCount = (name, value) => {
        document.querySelectorAll(`[data-comment-count="${name}"]`).forEach((element) => {
            element.textContent = value;
        });
    };

    const updateStats = (stats) => {
        if (!stats) {
            return;
        }
        updateCount('total', stats.total);
        updateCount('good', stats.good);
        updateCount('neutral', stats.neutral);
        updateCount('bad', stats.bad);
    };

    const createElement = (tag, className, text) => {
        const element = document.createElement(tag);
        if (className) {
            element.className = className;
        }
        if (text !== undefined && text !== null) {
            element.textContent = text;
        }
        return element;
    };

    const shouldDisplayComment = (comment) => {
        const selectedRating = commentsList.dataset.selectedRating || 'ALL';
        return selectedRating === 'ALL' || selectedRating === comment.rating;
    };

    const createCommentCard = (comment) => {
        const article = createElement('article', 'comment-card comment-card-new');
        article.dataset.commentRating = comment.rating;

        const top = createElement('div', 'comment-card-top');
        const avatar = createElement('div', 'comment-avatar', comment.authorInitial || '?');
        const identity = createElement('div');
        identity.appendChild(createElement('strong', '', comment.authorName || '买家'));
        identity.appendChild(createElement('time', '', comment.createdAt || '刚刚'));

        const ratingBadge = createElement(
            'span',
            `comment-rating-badge rating-${String(comment.rating || 'GOOD').toLowerCase()}`,
            comment.ratingLabel || '好评'
        );
        const freshBadge = createElement('span', 'comment-fresh-badge', '刚刚发布');

        top.appendChild(avatar);
        top.appendChild(identity);
        top.appendChild(ratingBadge);
        top.appendChild(freshBadge);

        const content = createElement('p', 'comment-content', comment.content);

        const actions = createElement('div', 'comment-actions');
        const likeCount = createElement('span', 'comment-like-count');
        const likeIcon = createElement('i', 'bi bi-hand-thumbs-up');
        const likeNumber = createElement('span', '', comment.likeCount ?? 0);
        likeCount.appendChild(likeIcon);
        likeCount.appendChild(likeNumber);
        actions.appendChild(likeCount);
        actions.appendChild(createElement('span', 'small text-muted', '你的评论已展示在当前页面'));

        article.appendChild(top);
        article.appendChild(content);
        article.appendChild(actions);
        return article;
    };

    const cloneChildren = (source) => Array.from(source.childNodes).map((node) => node.cloneNode(true));

    const setFilterLoading = (loading) => {
        const filterTabs = document.querySelector('[data-rating-filter-tabs]');
        const activeList = document.querySelector('[data-comments-list]');

        if (filterTabs) {
            filterTabs.setAttribute('aria-busy', String(loading));
        }
        if (activeList) {
            activeList.classList.toggle('is-loading', loading);
        }
    };

    const replaceRatingFilterContent = (documentFragment) => {
        const currentTabs = document.querySelector('[data-rating-filter-tabs]');
        const nextTabs = documentFragment.querySelector('[data-rating-filter-tabs]');
        const currentList = document.querySelector('[data-comments-list]');
        const nextList = documentFragment.querySelector('[data-comments-list]');

        if (!currentTabs || !nextTabs || !currentList || !nextList) {
            throw new Error('暂时无法更新评价筛选');
        }

        currentTabs.replaceChildren(...cloneChildren(nextTabs));
        currentList.dataset.selectedRating = nextList.dataset.selectedRating || 'ALL';
        currentList.replaceChildren(...cloneChildren(nextList));
    };

    document.addEventListener('click', async (event) => {
        const link = event.target.closest('[data-rating-filter-link]');
        if (!link || event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
            return;
        }

        event.preventDefault();

        if (link.classList.contains('active')) {
            return;
        }

        setFilterLoading(true);

        try {
            const response = await fetch(link.href, {
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) {
                throw new Error('评价筛选加载失败');
            }

            const html = await response.text();
            const nextDocument = new DOMParser().parseFromString(html, 'text/html');
            replaceRatingFilterContent(nextDocument);
            history.replaceState(null, '', link.href);
        } catch (error) {
            window.location.href = link.href;
        } finally {
            setFilterLoading(false);
        }
    });

    if (!form || !commentsList) {
        return;
    }

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const button = form.querySelector('button[type="submit"]');
        const originalButtonHtml = button ? button.innerHTML : '';

        if (button) {
            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm me-1" aria-hidden="true"></span>发布中';
        }

        try {
            const response = await fetch(form.action, {
                method: 'POST',
                body: new FormData(form),
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Accept': 'application/json'
                }
            });
            const contentType = response.headers.get('content-type') || '';
            if (!contentType.includes('application/json')) {
                throw new Error('登录状态已失效，请重新登录');
            }
            const data = await response.json().catch(() => ({}));
            if (!response.ok || data.success === false) {
                throw new Error(data.message || '评论发布失败');
            }

            if (data.comment && shouldDisplayComment(data.comment)) {
                const empty = commentsList.querySelector('[data-comment-empty]');
                if (empty) {
                    empty.remove();
                }
                commentsList.prepend(createCommentCard(data.comment));
            }
            updateStats(data.stats);
            form.reset();
            showMessage(data.message || '评论已发布');
        } catch (error) {
            showMessage(error.message || '评论发布失败', false);
        } finally {
            if (button) {
                button.disabled = false;
                button.innerHTML = originalButtonHtml;
            }
        }
    });
});
