// ============================================================
// config.js — 全局配置 & 工具函数
// 所有页面通过 <script src="config.js"> 引入
// ============================================================

const CONFIG = {
    BASE_URL: 'http://localhost:8080/api',  // ← 后端地址，按需修改
    TOKEN_KEY: 'lab_token',
    USER_KEY:  'lab_user'
}

// ==================== Token / 用户信息 ====================

const Auth = {
    getToken()    { return localStorage.getItem(CONFIG.TOKEN_KEY) },
    setToken(t)   { localStorage.setItem(CONFIG.TOKEN_KEY, t) },
    getUser()     { return JSON.parse(localStorage.getItem(CONFIG.USER_KEY) || 'null') },
    setUser(u)    { localStorage.setItem(CONFIG.USER_KEY, JSON.stringify(u)) },
    clear()       { localStorage.removeItem(CONFIG.TOKEN_KEY); localStorage.removeItem(CONFIG.USER_KEY) },
    isLoggedIn()  { return !!this.getToken() },
    isAdmin()     { return this.getUser()?.role === 'ADMIN' },
    isTeacher()   { return this.getUser()?.role === 'TEACHER' },
    isStudent()   { return this.getUser()?.role === 'STUDENT' },
    isTeacherOrAdmin() { return ['TEACHER','ADMIN'].includes(this.getUser()?.role) }
}

// ==================== HTTP 请求封装 ====================

const Http = {
    async request(method, path, data = null, params = null) {
        const url = new URL(CONFIG.BASE_URL + path)
        if (params) Object.entries(params).forEach(([k,v]) => v != null && url.searchParams.set(k, v))

        const options = {
            method,
            headers: { 'Content-Type': 'application/json' }
        }
        const token = Auth.getToken()
        if (token) options.headers['Authorization'] = `Bearer ${token}`
        if (data)  options.body = JSON.stringify(data)

        try {
            const resp = await fetch(url.toString(), options)
            const json = await resp.json()

            if (json.code === 200) return json
            if (json.code === 401) { Auth.clear(); window.location.href = 'login.html'; return }
            Toast.error(json.message || '请求失败')
            return null
        } catch (e) {
            Toast.error('网络连接失败，请检查后端服务')
            return null
        }
    },
    get(path, params)   { return this.request('GET',    path, null, params) },
    post(path, data)    { return this.request('POST',   path, data) },
    put(path, data, params) { return this.request('PUT', path, data, params) }
}

// ==================== API ====================

const API = {
    // 认证
    login:    (d)  => Http.post('/auth/login', d),
    register: (d)  => Http.post('/auth/register', d),

    // 仪表盘
    dashboard: ()  => Http.get('/dashboard'),

    // 耗材
    supplyList:    (p)    => Http.get('/supply/list', p),
    supplyDetail:  (id)   => Http.get(`/supply/${id}`),
    supplyAdd:     (d)    => Http.post('/supply', d),
    supplyUpdate:  (d)    => Http.put('/supply', d),
    supplyStatus:  (id,s) => Http.put(`/supply/${id}/status`, null, {status: s}),

    // 分类
    categoryList: () => Http.get('/category/list'),

    // 库存
    stockIn:     (d) => Http.post('/stock/in', d),
    stockInList: (p) => Http.get('/stock/in/list', p),
    stockOutList:(p) => Http.get('/stock/out/list', p),

    // 申请审批
    apply:           (d) => Http.post('/stock/apply', d),
    approve:         (d) => Http.post('/stock/approve', d),
    applicationList: (p) => Http.get('/stock/application/list', p),

    // 用户
    userList:    ()      => Http.get('/user/list'),
    userTeachers:()      => Http.get('/user/teachers'),
    userMe:      ()      => Http.get('/user/me'),
    userStatus:  (id, s) => Http.put(`/user/${id}/status`, null, {status: s})
}

// ==================== Toast 提示 ====================

const Toast = {
    show(msg, type = 'info') {
        const el = document.createElement('div')
        el.className = `toast toast-${type}`
        el.innerHTML = `<span class="toast-icon">${{success:'✓',error:'✕',warning:'⚠',info:'ℹ'}[type]}</span><span>${msg}</span>`
        document.body.appendChild(el)
        requestAnimationFrame(() => el.classList.add('show'))
        setTimeout(() => { el.classList.remove('show'); setTimeout(() => el.remove(), 300) }, 3000)
    },
    success: (m) => Toast.show(m, 'success'),
    error:   (m) => Toast.show(m, 'error'),
    warning: (m) => Toast.show(m, 'warning'),
    info:    (m) => Toast.show(m, 'info')
}

// ==================== Modal 对话框 ====================

const Modal = {
    /**
     * 显示一个弹窗
     * @param {string} title
     * @param {string} bodyHtml  - 弹窗内容HTML
     * @param {Function} onConfirm - 确认回调，返回false不关闭
     */
    show(title, bodyHtml, onConfirm, confirmText = '确 定') {
        document.getElementById('modal-overlay')?.remove()
        const overlay = document.createElement('div')
        overlay.id = 'modal-overlay'
        overlay.innerHTML = `
      <div class="modal-box">
        <div class="modal-header">
          <span>${title}</span>
          <button class="modal-close" onclick="Modal.close()">✕</button>
        </div>
        <div class="modal-body">${bodyHtml}</div>
        <div class="modal-footer">
          <button class="btn btn-ghost" onclick="Modal.close()">取 消</button>
          <button class="btn btn-primary" id="modal-confirm-btn">${confirmText}</button>
        </div>
      </div>
    `
        document.body.appendChild(overlay)
        requestAnimationFrame(() => overlay.classList.add('show'))
        document.getElementById('modal-confirm-btn').onclick = async () => {
            const result = await onConfirm()
            if (result !== false) Modal.close()
        }
    },
    confirm(msg, onConfirm) {
        Modal.show('确认操作', `<p style="padding:16px 0;color:#5a6a7a">${msg}</p>`, onConfirm, '确 认')
    },
    close() {
        const o = document.getElementById('modal-overlay')
        if (o) { o.classList.remove('show'); setTimeout(() => o.remove(), 250) }
    }
}

// ==================== 分页组件 ====================

function renderPagination(containerId, pageInfo, onPageChange) {
    const c = document.getElementById(containerId)
    if (!c || !pageInfo) return
    const { pageNum, pages, total } = pageInfo
    if (pages <= 1) { c.innerHTML = ''; return }
    let btns = ''
    btns += `<button class="page-btn" ${pageNum<=1?'disabled':''} onclick="(${onPageChange})(${pageNum-1})">‹</button>`
    for (let i = 1; i <= pages; i++) {
        if (pages > 7 && Math.abs(i - pageNum) > 2 && i !== 1 && i !== pages) {
            if (i === 2 || i === pages - 1) btns += `<span class="page-ellipsis">…</span>`
            continue
        }
        btns += `<button class="page-btn ${i===pageNum?'active':''}" onclick="(${onPageChange})(${i})">${i}</button>`
    }
    btns += `<button class="page-btn" ${pageNum>=pages?'disabled':''} onclick="(${onPageChange})(${pageNum+1})">›</button>`
    c.innerHTML = `<div class="pagination"><span class="page-total">共 ${total} 条</span>${btns}</div>`
}

// ==================== 工具函数 ====================

function escHtml(str) {
    if (!str) return ''
    return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;')
}
function fmtDate(str) {
    if (!str) return '-'
    return str.replace('T',' ').substring(0, 16)
}
function statusTag(status) {
    const map = { PENDING: ['待审批','tag-warning'], APPROVED: ['已通过','tag-success'], REJECTED: ['已拒绝','tag-danger'] }
    const [label, cls] = map[status] || [status, 'tag-info']
    return `<span class="tag ${cls}">${label}</span>`
}
function roleTag(role) {
    const map = { ADMIN: ['管理员','tag-danger'], TEACHER: ['教师','tag-warning'], STUDENT: ['学生','tag-success'] }
    const [label, cls] = map[role] || [role, 'tag-info']
    return `<span class="tag ${cls}">${label}</span>`
}

// 路由守卫：所有页面（除 login.html）都需要登录
if (!window.location.pathname.endsWith('login.html')) {
    if (!Auth.isLoggedIn()) window.location.href = 'login.html'
}