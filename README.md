# VoidEcho 🌌

**VoidEcho** is an intense, high-stakes Minecraft mini-game plugin where players battle for survival on a shifting, ethereal arena. The ground beneath your feet is temporary—only the rhythm of the **Echo Pulse** and your own aim can keep you from the void.

---

## 🎮 Gameplay Mechanics

### The Concept
Players spawn in a void world. The goal is simple: be the last player standing. However, the arena floor is **not permanent**.

### 🌟 The Echo Pulse (The Cycle)
The arena operates on a strict 7-second cycle:
1.  **Pulse Active (5 Seconds):** The entire arena floor materializes as **Solid White Concrete**. A sonic boom echoes, and the arena is safe to walk on.
2.  **The Void (2 Seconds):** The floor vanishes completely, turning to **AIR**. Anyone not standing on a created platform will fall.

### ❄️ Snowball Platforms
Your primary tool for survival and offense.
- **Impact:** When a snowball hits a player or an entity, it manifests a **White Concrete Block** at `Y=100` beneath the impact point.
- **Persistence:** These blocks are temporary sanctuaries. They persist for exactly **10 seconds** before disappearing.
- **Strategy:** Use snowballs to create bridges during the Void phase or to trap opponents.

### ⚔️ Loadout
Every player starts with a kit designed for knockback and mobility:
- **64x Snowballs:** Your lifeline for creating platforms.
- **Fishing Rod (Knockback II):** Use it to pull enemies into the void or push them off their safety blocks.

### 💀 Elimination
- Falling below **Y=85** results in immediate elimination.
- Eliminated players are safely teleported to the main world lobby, fully healed and reset.
- The last player remaining is the **Winner**.

---

## 📜 Commands

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/ve join` | Join the game lobby. | None |
| `/ve leave` | Leave the current game. | None |
| `/ve arena` | Teleport to the spectator/lobby area. | None |
| `/ve setarena` | Set the center point of the game arena at your location. | `voidecho.admin` |
| `/ve start` | Force start the game immediately. | `voidecho.admin` |
| `/ve stop` | Force stop the game and reset everyone. | `voidecho.admin` |

**Alias:** `/voidecho`

---

## 🛠️ Installation & Setup

1.  **Download:** Place the `VoidEcho-1.0.0.jar` into your server's `plugins` folder.
2.  **Restart:** Restart your Spigot/Paper server.
3.  **Set Arena:**
    *   Go to your desired void world or location.
    *   Stand at the center point (recommended Y=100 for gameplay consistency).
    *   Run `/ve setarena`.
4.  **Play:**
    *   Players can join via `/ve join`.
    *   The game auto-starts at **12 players**, or an admin can force start with `/ve start`.

---

## ⚙️ Configuration
The `config.yml` handles the arena location storage.

```yaml
arena:
  center:
    x: 0.5
    y: 100.0
    z: 0.5
```

---

## 📋 Requirements
*   **Java:** Java 17 or higher.
*   **Server:** Spigot, Paper, or Purpur (1.19+ recommended for Sonic Boom sounds and Warden effects).

---

*VoidEcho - Can you hear the pulse before the fall?*
