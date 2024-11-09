package fun.miranda.controller;

import fun.miranda.utils.Lists;
import fun.miranda.utils.Strings;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static fun.miranda.MeowCOC.plugin;

public class PlayerCard {
    private String name = "";
    private YamlConfiguration config = null;
    private File cardFile = null;

    public PlayerCard(String cardName) {
        this.cardFile = new File(plugin.getDataFolder(), String.format("/cards/%s.yml", cardName));
        this.config = YamlConfiguration.loadConfiguration(cardFile);
        this.name = cardName;
    }

    public String getName() {
        return this.name;
    }

    public Integer getStatus(String status) {
        return this.config.getInt(String.format("status.%s", status), 0);
    }

    public Integer getSkill(String skill) {
        return this.config.getInt(String.format("skills.%s", skill), 0);
    }

    public Integer getAttr(String attr) {
        return this.config.getInt(String.format("attrs.%s", attr), 0);
    }

    public String showCard() {
        StringBuilder out = new StringBuilder();
        out.append(String.format(Strings.ShowCharName, this.name));
        out.append(Strings.ShowStatus);
        Integer HP = this.getResult(Strings.HP);
        Integer maxHP = this.getResult(Strings.maxHP);
        Integer MP = this.getResult(Strings.MP);
        Integer maxMP = this.getResult(Strings.maxMP);
        Integer san = this.getResult(Strings.SAN);
        out.append(String.format(Strings.ShowStatusString, HP, maxHP, MP, maxMP, san));
        out.append(Strings.ShowAttrs);
        HashMap<String, Integer> attrMap = new HashMap<>();
        for (String attr : this.config.getConfigurationSection("attrs").getKeys(false)) {
            attrMap.put(attr, this.config.getInt(String.format("attrs.%s", attr)));
        }
        int lineAttr = 0;
        for (Map.Entry<String, Integer> entry : attrMap.entrySet()) {
            out.append(String.format(Strings.ShowAttrAndSkillString, entry.getKey(), entry.getValue()));
            lineAttr++;
            if (lineAttr == 5) {
                out.append("\n");
                lineAttr = 0;
            }

        }
        out.append("\n");
        out.append(Strings.ShowSkills);
        HashMap<String, Integer> skillMap = new HashMap<>();
        for (String attr : this.config.getConfigurationSection("skills").getKeys(false)) {
            skillMap.put(attr, this.config.getInt(String.format("skills.%s", attr)));
        }
        int lineSkill = 0;
        for (Map.Entry<String, Integer> entry : skillMap.entrySet()) {
            out.append(String.format(Strings.ShowAttrAndSkillString, entry.getKey(), entry.getValue()));
            lineSkill++;
            if (lineSkill == 4) {
                out.append("\n");
                lineSkill = 0;
            }

        }
        return out.toString();
    }

    public List<String> getSkills() {
        Set<String> tempSkills = Objects.requireNonNull(this.config.getConfigurationSection("skills")).getKeys(false);
        return new ArrayList<>(tempSkills);
    }

    public Integer getResult(String key) {
        Integer result;
        if (Lists.attrs.contains(key)) {
            result = this.getAttr(key);
        } else if (Lists.status.contains(key)) {
            result = this.getStatus(key);
        } else if (this.getSkills().contains(key)) {
            result = this.getSkill(key);
        } else {
            result = null;
        }
        return result;
    }

    public void setStatus(String status, Integer value) {
        this.config.set(String.format("status.%s", status), value);
        this.save();
    }

    public void setSkill(String skill, Integer value) {
        this.config.set(String.format("skills.%s", skill), value);
        this.save();
    }

    public void setAttr(String attr, Integer value) {
        this.config.set(String.format("attrs.%s", attr), value);
        this.save();
    }

    public Integer set(String key, Integer value) {
        if (Lists.attrs.contains(key)) {
            this.setAttr(key, value);
        } else if (Lists.status.contains(key)) {
            this.setStatus(key, value);
        } else {
            this.setSkill(key, value);
        }
        return this.getResult(key);
    }

    public Integer setWithSymbol(String key, String value) {
        Integer result = this.getResult(key);
        if (result == null) {
            return null;
        }
        Integer change = Integer.parseInt(value);
        int adjusted = result + change;
        if (adjusted < 0) {
            adjusted = 0;
        }
        this.set(key, adjusted);
        return this.getResult(key);
    }

    private void save() {
        try {
            this.config.save(this.cardFile);
        } catch (IOException ignored) {
        }
    }
}
