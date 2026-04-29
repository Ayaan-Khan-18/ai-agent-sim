// ── Speed Control ──
let eventDelay = 130;

function initSpeed() {
  const saved = localStorage.getItem('eventDelay');
  if (saved) {
    eventDelay = parseInt(saved);
    document.getElementById('speedSlider').value = eventDelay;
  }
  updateSpeedLabel();
}

function updateSpeedLabel() {
  document.getElementById('speedValue').textContent = eventDelay + 'ms';
}

function setEventSpeed(ms) {
  eventDelay = ms;
  localStorage.setItem('eventDelay', ms);
  updateSpeedLabel();
}

// ── Theme Toggle ──
function initTheme() {
  const saved = localStorage.getItem('theme') || 'dark';
  document.documentElement.setAttribute('data-theme', saved);
  updateThemeIcon(saved);
}

function updateThemeIcon(theme) {
  const btn = document.getElementById('themeToggle');
  btn.textContent = theme === 'dark' ? '☀️' : '🌙';
}

function toggleTheme() {
  const html = document.documentElement;
  const current = html.getAttribute('data-theme') || 'dark';
  const next = current === 'dark' ? 'light' : 'dark';
  html.setAttribute('data-theme', next);
  localStorage.setItem('theme', next);
  updateThemeIcon(next);
}

// Initialize theme and speed on page load
initTheme();
initSpeed();

const $id = id => document.getElementById(id);
const inp = $id('userInput');
const sendBtn = $id('sendBtn');
const messagesEl = $id('messages');
const eventsLog = $id('eventsLog');
const suggestionsEl = $id('suggestions');

let turns = 0, tools = 0, events = 0;
let memoryLog = [];

// ── Textarea auto-resize ──
inp.addEventListener('keydown', e => {
  if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); sendMessage(); }
});
inp.addEventListener('input', () => {
  inp.style.height = 'auto';
  inp.style.height = Math.min(inp.scrollHeight, 130) + 'px';
});

// ── Tab switch ──
function switchTab(name, el) {
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
  el.classList.add('active');
  $id('tab-' + name).classList.add('active');
}

// ── Quick send ──
function quickSend(text) {
  inp.value = text;
  inp.dispatchEvent(new Event('input'));
  sendMessage();
}

// ── State machine ──
function setState(state) {
  ['sn-idle','sn-think','sn-act'].forEach(id => $id(id).classList.remove('is-active'));
  const map = { IDLE:'sn-idle', THINKING:'sn-think', ACTING:'sn-act' };
  if (map[state]) $id(map[state]).classList.add('is-active');
  $id('mState').textContent = state === 'THINKING' ? 'THINK' : state;
}

// ── Metrics ──
function updateMetrics() {
  $id('hTurns').textContent  = turns;
  $id('hTools').textContent  = tools;
  $id('hEvents').textContent = events;
  $id('mTurns').textContent  = turns;
  $id('mTools').textContent  = tools;
  $id('mEvents').textContent = events;
  $id('logCount').textContent = events + ' event' + (events !== 1 ? 's' : '');
}

// ── Tool detection ──
const toolMap = {
  calculator:  'tool-calculator',
  filereader:  'tool-filereader',
  webcrawler:  'tool-webcrawler',
  emailsender: 'tool-emailsender',
};
function detectTool(evText) {
  const lower = evText.toLowerCase();
  for (const [key, id] of Object.entries(toolMap)) {
    if (lower.includes(key)) {
      const card = $id(id);
      if (card) {
        card.classList.add('firing');
        setTimeout(() => card.classList.remove('firing'), 1600);
      }
      tools++;
      const ltc = $id('lastToolCard');
      ltc.classList.add('has-tool');
      $id('lastToolText').textContent = key;
      updateMetrics();
      return;
    }
  }
}

// ── Event type classifier ──
function classifyEvent(text) {
  if (/thinking|THINKING/i.test(text))        return ['thinking', '🧠'];
  if (/acting|ACTING/i.test(text))            return ['acting',   '⚡'];
  if (/tool|Tool|Using|using/i.test(text))    return ['tool',     '🔧'];
  if (/user said|User/i.test(text))           return ['user_ev',  '💬'];
  if (/result|Result|complete|Complete/i.test(text)) return ['result', '✅'];
  if (/idle|IDLE/i.test(text))                return ['idle',     '💤'];
  return ['idle', 'ℹ'];
}

// ── Time ──
function ts() {
  return new Date().toLocaleTimeString([], { hour:'2-digit', minute:'2-digit', second:'2-digit' });
}

// ── Add event card ──
function addEvent(text) {
  const empty = $id('emptyEvents');
  if (empty) empty.remove();

  events++;
  updateMetrics();

  const [cls, icon] = classifyEvent(text);
  const div = document.createElement('div');
  div.className = 'event ' + cls;
  div.innerHTML = `
    <div class="event-ico">${icon}</div>
    <div class="event-body">
      <div class="event-text">${text}</div>
      <div class="event-time">${ts()}</div>
    </div>`;
  eventsLog.appendChild(div);
  eventsLog.scrollTop = eventsLog.scrollHeight;
}

// ── Clear events ──
function clearEvents() {
  eventsLog.innerHTML = '';
  events = 0;
  updateMetrics();
}

// ── Add chat message ──
function addMessage(role, html) {
  const div = document.createElement('div');
  div.className = 'message ' + role;
  div.innerHTML = `
    <div class="avatar">${role === 'user' ? 'ME' : 'AI'}</div>
    <div class="bubble-wrap">
      <div class="bubble-meta">${role === 'user' ? 'You' : 'Agent'} · ${ts()}</div>
      <div class="bubble">${html}</div>
    </div>`;
  messagesEl.appendChild(div);
  messagesEl.scrollTop = messagesEl.scrollHeight;
}

// ── Typing indicator ──
function showTyping() {
  const div = document.createElement('div');
  div.className = 'message agent';
  div.id = 'typing-msg';
  div.innerHTML = `
    <div class="avatar">AI</div>
    <div class="bubble-wrap">
      <div class="bubble-meta">Agent · thinking</div>
      <div class="typing-indicator">
        <div class="typing-dot"></div>
        <div class="typing-dot"></div>
        <div class="typing-dot"></div>
      </div>
    </div>`;
  messagesEl.appendChild(div);
  messagesEl.scrollTop = messagesEl.scrollHeight;
  return div;
}

// ── Memory ──
function updateMemory() {
  const pane = $id('memoryPane');
  if (!memoryLog.length) return;
  pane.innerHTML = memoryLog.slice(-12).map(m => `
    <div class="memory-entry mem-${m.role}">
      <div class="role-tag">${m.role}</div>
      <div class="mem-text">${m.text}</div>
    </div>`).join('');
}

// ── Speed Control Event ──
$id('speedSlider').addEventListener('input', (e) => {
  setEventSpeed(parseInt(e.target.value));
});

// ── Theme Toggle Event ──
$id('themeToggle').addEventListener('click', toggleTheme);

// ── Send ──
async function sendMessage() {
  const text = inp.value.trim();
  if (!text) return;

  inp.value = '';
  inp.style.height = 'auto';
  sendBtn.disabled = true;
  suggestionsEl.style.display = 'none';

  turns++;
  updateMetrics();

  addMessage('user', text);
  memoryLog.push({ role: 'user', text: text.slice(0, 70) + (text.length > 70 ? '…' : '') });
  updateMemory();

  setState('THINKING');
  addEvent('State → THINKING');

  const typing = showTyping();

  try {
    const res = await fetch('/chat', { method: 'POST', body: text });
    const data = await res.json();
    typing.remove();

    data.events.forEach((ev, i) => {
      setTimeout(() => {
        addEvent(ev);
        detectTool(ev);
        if (/acting|ACTING/i.test(ev))  setState('ACTING');
        if (/idle|IDLE/i.test(ev))      setState('IDLE');
      }, i * eventDelay);
    });

    setTimeout(() => {
      setState('IDLE');
      addEvent('State → IDLE');
      addMessage('agent', data.response);
      memoryLog.push({ role: 'agent', text: data.response.slice(0, 70) + (data.response.length > 70 ? '…' : '') });
      updateMemory();
      sendBtn.disabled = false;
      inp.focus();
    }, data.events.length * eventDelay + 200);

  } catch (err) {
    typing.remove();
    setState('IDLE');
    addEvent('ERROR: ' + err.message);
    addMessage('agent', '⚠ Something went wrong. Please try again.');
    sendBtn.disabled = false;
    inp.focus();
  }
  
}
