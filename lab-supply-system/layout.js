// layout.js — 生成通用侧边栏 + 顶栏，所有页面引入
// 用法：<script src="layout.js"></script>，在 body 最开始调用 Layout.render('当前页面标识')

const Layout = {
    // 菜单定义
    menus: [
        { key:'dashboard',   href:'dashboard.html',   icon:'🏠', label:'首页总览',  roles:['ADMIN','TEACHER','STUDENT'] },
        { key:'supply',      href:'supply.html',       icon:'📦', label:'耗材管理',  roles:['ADMIN','TEACHER','STUDENT'] },
        { key:'stockin',     href:'stockin.html',      icon:'📥', label:'入库管理',  roles:['ADMIN'] },
        { key:'application', href:'application.html',  icon:'📋', label:'领用申请',  roles:['STUDENT','TEACHER'] },
        { key:'approve',     href:'approve.html',      icon:'✅', label:'审批管理',  roles:['ADMIN','TEACHER'] },
        { key:'stockout',    href:'stockout.html',     icon:'📤', label:'出库记录',  roles:['ADMIN','TEACHER'] },
        { key:'user',        href:'user.html',         icon:'👥', label:'用户管理',  roles:['ADMIN'] },
    ],

    pendingCount: 0,
    lowStockCount: 0,

    render(activeKey) {
        const user = Auth.getUser()
        if (!user) return

        const role = user.role
        const filteredMenus = this.menus.filter(m => m.roles.includes(role))

        const navHtml = filteredMenus.map(m => `
      <a href="${m.href}" class="nav-link ${m.key === activeKey ? 'active' : ''}">
        <span class="nav-icon">${m.icon}</span>
        <span>${m.label}</span>
        ${m.key === 'approve' ? `<span class="nav-badge" id="nav-pending-badge" style="display:none"></span>` : ''}
      </a>
    `).join('')

        const roleLabels = { ADMIN:'管理员', TEACHER:'教师', STUDENT:'学生' }
        const roleColors = { ADMIN:'tag-danger', TEACHER:'tag-warning', STUDENT:'tag-success' }

        const sidebarHtml = `
      <aside class="sidebar">
        <div class="sidebar-logo">
          <div class="logo-icon">⚗️</div>
          <div>
            <div class="logo-text">耗材管理系统</div>
            <div class="logo-sub">Major-Link Project</div>
          </div>
        </div>
        <nav class="sidebar-nav">${navHtml}</nav>
        <div class="sidebar-footer">
          <div class="user-card">
            <div class="user-avatar">${(user.realName || user.username)[0]}</div>
            <div>
              <div class="user-name">${user.realName || user.username}</div>
              <span class="tag ${roleColors[role]}">${roleLabels[role]}</span>
            </div>
          </div>
          <div class="sidebar-logout" onclick="Layout.logout()">
            <span>🚪</span> 退出登录
          </div>
        </div>
      </aside>
    `
        // 插入侧边栏到 .layout 容器
        const layout = document.querySelector('.layout')
        if (layout) layout.insertAdjacentHTML('afterbegin', sidebarHtml)

        // 异步拉取首页数据，更新预警角标
        this.fetchBadges()
    },

    async fetchBadges() {
        try {
            const res = await API.dashboard()
            if (!res) return
            this.pendingCount  = res.data.pendingApplicationCount || 0
            this.lowStockCount = res.data.lowStockCount || 0

            // 更新审批角标
            const badge = document.getElementById('nav-pending-badge')
            if (badge && this.pendingCount > 0) {
                badge.textContent = this.pendingCount
                badge.style.display = 'flex'
            }

            // 更新顶栏预警
            const pill = document.getElementById('header-warning-pill')
            if (pill && this.lowStockCount > 0) {
                pill.style.display = 'flex'
                pill.querySelector('.warn-count').textContent = `${this.lowStockCount} 项预警`
            }
        } catch {}
    },

    logout() {
        if (!confirm('确认退出登录？')) return
        Auth.clear()
        window.location.href = 'login.html'
    }
}