<p align="center">
<img src="https://dwarslooper.com/images/dl.jpg" alt="dl-logo" width="15%"/>
</p>

<h1 align="center">Defend Luetzerath</h1>
<p align="center">A Minecraft minigame made in a week or so</p>

<div align="center">
    <a href="https://dsc.gg/dwars"><img src="https://img.shields.io/discord/687682739297845269?logo=discord&style=for-the-badge" alt="Discord"/></a>
    <br>
    <img src="https://img.shields.io/github/downloads/Dwarslooper/Defend-Luetzerath/total?style=for-the-badge" alt="Releases"/>
    <img src="https://img.shields.io/github/last-commit/Dwarslooper/Defend-Luetzerath?style=for-the-badge" alt="Last commit"/>
    <img src="https://img.shields.io/github/issues/Dwarslooper/Defend-Luetzerath?style=for-the-badge" alt="Issues open"/>
</div>

## Usage

### Installation
1. Download the latest release from the [releases](https://github.com/Dwarslooper/Invite-Scanner/releases) page.
2. Put the file in the plugins folder of your server.
3. Restart your server

### Dependencies
- [Multiverse-Core](https://dev.bukkit.org/projects/multiverse-core)
- [Fast-Async-Worldedit](https://www.spigotmc.org/resources/fastasyncworldedit.13932/)

⚠️ _Make sure to have both plugins installed on their latest versions!_ ⚠️

## Contributions
We will review and help with all reasonable pull requests as long as you follow the guidelines below:

- IDE or system-related files should be added to the `.gitignore`, never committed in pull requests.
- If you change existing code, make sure it matches relatively close with the original code.
- Favour readability over compactness.
- If you add Characters, initialize them with the Character Manager and a matching class extending a Character-Type class.
- Character behaviour must only be defined in the `tick()` method of the Character's class.
- Notice that all characters extending `Enemy` will spawn automatically
