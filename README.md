# Template Module - Gids voor Module Development

Dit is een **template project** om je eigen PlugModCore modules te maken. Volg deze stappen om aan de slag te gaan.

## 🚀 Snel Starten

### 1. **Clone/Kopieer dit Project**
```bash
# Copy deze folder en hernoem naar je module naam
cp -r TemplateModule MyAwesomeModule
cd MyAwesomeModule
```

### 2. **Pas de Module Info aan**

**Stap 1: Update `pom.xml`**
```xml
<artifactId>MyAwesomeModule</artifactId>  <!-- Hernoem dit -->
<version>1.0.0</version>                   <!-- Versie aanpassen -->
```

**Stap 2: Update `src/main/resources/module.info.yml`**
```yaml
id: myawesomemodule              # Unieke module ID (lowercase, no spaces)
name: My Awesome Module          # Display naam
version: 1.0.0                   # Versie
main: org.wannes.MyAwesomeModule # Main class locatie
description: "Wat doet je module?"
```

**Stap 3: Hernoem Java klasse**
```bash
# Hernoem TemplateModule.java naar je module naam
# Bijv. MyAwesomeModule.java
mv src/main/java/org/wannes/TemplateModule.java src/main/java/org/wannes/MyAwesomeModule.java
```

Update als de class reference in de file:
```java
public class MyAwesomeModule implements Module {
    // ...
}
```

### 3. **Implementeer je Module Logica**

Open `src/main/java/org/wannes/MyAwesomeModule.java` en voeg je logica toe:

```java
@Override
public void onEnable(ModuleContext context) {
    // Hier wordt je module gestart
    // - Config files laden
    // - Event listeners registreren
    // - Commands setup
    // - Database init
    // etc
}

@Override
public void onDisable() {
    // Hier wordt je module gestopt
    // - Resources opschonen
    // - Threads stoppen
    // - Database sluiten
}
```

### 4. **Compileer je Module**

```bash
# In de TemplateModule folder
mvn clean package
```

Dit creëert `target/MyAwesomeModule-1.0-SNAPSHOT.jar`

### 5. **Deploy je Module**

Copy de JAR naar PlugModCore:
```bash
cp target/MyAwesomeModule-1.0-SNAPSHOT.jar ../path/to/server/plugins/PlugModCore/modules/
```

### 6. **Laad je Module**

In-game (als je server draait):
```
/module load myawesomemodule
```

Of automatisch bij startup (Module wordt opgeslagen als geladen)

---

## 📚 Basis Structuur (wat je moet weten)

### **File Structure**
```
MyAwesomeModule/
├── pom.xml                          # Maven build configuratie
├── src/
│   ├── main/
│   │   ├── java/org/wannes/
│   │   │   └── MyAwesomeModule.java # Je module klasse
│   │   └── resources/
│   │       ├── module.info.yml      # Module metadata
│   │       └── web/                 # (optioneel) Web UI assets
│   │           ├── index.html
│   │           ├── styles.css
│   │           └── script.js
│   └── test/
│       └── java/org/wannes/         # Tests (optioneel)
└── target/
    └── MyAwesomeModule-1.0-SNAPSHOT.jar  # Gecompileerde JAR
```

### **Module Lifecycle**

```
PlugModCore Start
       ↓
Scan modules/ folder
       ↓
Lees module.info.yml
       ↓
Load JAR (URLClassLoader)
       ↓
Instantieer MyAwesomeModule() → no-args constructor!
       ↓
Call onEnable(ModuleContext)
       ↓
Module is actief!
       ↓
On demand: onDisable()
```

**BELANGRIJK**: Je class moet een **no-args (lege) constructor** hebben!

```java
public class MyAwesomeModule implements Module {
    public MyAwesomeModule() {  // ← Dit MOET leeg zijn!
        // Niets hier
    }
}
```

---

## 🔧 Veel Gebruikte Scenarios

### **Scenario 1: Bukkit Event Listener (Minecraft events)**

```java
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welkom!");
    }
}

// In je MyAwesomeModule.java:
@Override
public void onEnable(ModuleContext context) {
    Bukkit.getPluginManager().registerEvents(
        new PlayerListener(), 
        Bukkit.getPluginManager().getPlugin("PlugModCore")
    );
}
```

### **Scenario 2: Config Files (in module-data folder)**

```java
@Override
public void onEnable(ModuleContext context) {
    // DataFolder is: plugins/PlugModCore/module-data/myawesomemodule/
    File configFile = new File(context.getDataFolder(), "config.yml");
    
    if (!configFile.exists()) {
        // Copy default config als het niet bestaat
        // Maak src/main/resources/config.yml
        try {
            context.getDataFolder().mkdirs();
            Files.copy(
                getClass().getResourceAsStream("/config.yml"),
                configFile.toPath()
            );
        } catch (IOException e) {
            context.getLogger().severe("Config error!");
        }
    }
    
    // Nu kun je config.yml laden met YAML parser
    // (bijv. SnakeYAML library)
}
```

### **Scenario 3: Web Interface (Dashboard)**

1. **Enable web in `module.info.yml`:**
```yaml
website:
  enabled: true
  entry: web/index.html
  title: "My Awesome Dashboard"
```

2. **Zet files in `src/main/resources/web/`:**
   - `index.html` - Je web page
   - `styles.css` - Styling
   - `script.js` - JavaScript logica

3. **Web API:**
   - Je files worden served op: `http://server:port/modules/myawesomemodule/web/`
   - Je kunt API calls doen naar PlugModCore web endpoints (zie PlugModCore documentatie)

### **Scenario 4: Logger Gebruiken**

```java
ModuleContext context = ...;
Logger logger = context.getLogger();

logger.info("Module started");
logger.warning("Dit is een waarschuwing");
logger.severe("Dit is een error");
```

---

## 📖 Module Interface (wat je MOET implementeren)

```java
public interface Module {
    /**
     * Called when module is enabled
     * @param context - gives access to logger, dataFolder, module info, etc
     */
    void onEnable(ModuleContext context);
    
    /**
     * Called when module is disabled
     * Do cleanup here!
     */
    void onDisable();
}
```

### ModuleContext Methods:
```java
String getModuleId()           // "myawesomemodule"
String getModuleName()         // "My Awesome Module"
String getVersion()            // "1.0.0"
Logger getLogger()             // Module logger
File getDataFolder()           // plugins/PlugModCore/module-data/myawesomemodule/
```

---

## 🐛 Troubleshooting

### **Probleem: "Module failed to load"**
**Oorzaak:** Waarschijnlijk een `ClassNotFoundException` of `NoSuchMethodException`

**Oplossing:**
- Check of je class naam in `module.info.yml` correct is
- Zorg dat je klasse een **no-args constructor** heeft
- Controleer dependencies in `pom.xml`
- Kijk in logs: `plugins/PlugModCore/logs/`

### **Probleem: "Module loads but does nothing"**
**Oorzaak:** Waarschijnlijk je `onEnable()` is leeg of heeft errors

**Oplossing:**
- Voeg logging toe aan `onEnable()` om te zien wat gebeurt
- Check logger output in server console
- Zorg dat alle try-catch blocks niet errors verbergen

### **Probleem: JAR is leeg**
**Oorzaak:** Maven build ging fout

**Oplossing:**
```bash
mvn clean package -X   # -X = debug output
```

---

## 📤 Dependencies Toevoegen

Voeg dependencies toe in `pom.xml`:

```xml
<dependencies>
    <!-- Bukkit/Paper API (al standaard voor ServerMod modules) -->
    <dependency>
        <groupId>io.papermc.paper</groupId>
        <artifactId>paper-api</artifactId>
        <version>1.21.11-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- JSON parsing (voorbeeld) -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
    
    <!-- GUI Library (voorbeeld) -->
    <dependency>
        <groupId>org.example</groupId>
        <artifactId>some-library</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

---

## ✅ Checklist voordat je publiceert

- [ ] `module.info.yml` is correct ingevuld
- [ ] `pom.xml` has correct `<main>` class path
- [ ] Java class has **no-args constructor**
- [ ] `onEnable()` en `onDisable()` zijn geïmplementeerd
- [ ] Module is getest (draait zonder errors)
- [ ] JAR file is gegenereerd (`target/*.jar`)
- [ ] Alle exceptions zijn gehandeld in try-catch blokken
- [ ] Logger is gebruikt ipv System.out.println()

---

## 📞 Hulp & Support

Kijk naar voorbeeldmodules:
- **ScoreBoard** - Eenvoudig voorbeeld met Bukkit integration
- Kijk naar PlugModCore source code voor web API endpoints

Veel succes met je module! 🎉
