package fr.flo504.spawner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Language {

    private final Map<String, String> lang;
    private final Logger logger;

    public Language(Plugin plugin, String fileName) {
        lang = new HashMap<>();
        logger = plugin.getLogger();

        final File langFolder = new File(plugin.getDataFolder(), "languages");

        langFolder.mkdirs();

        final File langFile = new File(langFolder, fileName);

        if(!langFile.exists()) {
            final InputStream stream = plugin.getResource(fileName);

            if (stream == null) {
                try {
                    langFile.createNewFile();
                } catch (IOException e) {
                    try {
                        throw e;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                try {
                    Files.copy(stream, langFile.toPath());
                } catch (IOException e) {
                    try {
                        throw e;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(langFile));
        } catch (FileNotFoundException e) {
            try {
                throw e;
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        final Iterator<String> lines = reader.lines().iterator();

        for(int index = 1; lines.hasNext(); index++){
            final String line = lines.next();

            if(line.replace(" ", "").startsWith("#")){
                continue;
            }

            if(line.replace(" ", "").equalsIgnoreCase("")){
                continue;
            }

            if(!line.contains("=")){
                logger.log(Level.WARNING, "Error at line "+index+": there is not '=' character");
                continue;
            }

            final int separatorIndex = line.indexOf("=");

            final String key = line.substring(0, separatorIndex);

            if(lang.containsKey(key)){
                logger.log(Level.WARNING, "Error at line "+index+": the key '"+key+"' is already use");
                continue;
            }

            final String value;

            final int subLen = line.length() - separatorIndex+1;
            if (subLen < 0) {
                value = "";
            }
            else{
                value = line.substring(separatorIndex+1).replace("\\n", "\n");
            }

            lang.put(key, ChatColor.translateAlternateColorCodes('&', value));
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public final String getMessage(String key, String... replace){

        if(!lang.containsKey(key)){
            logger.log(Level.WARNING, "Error the key "+key+" does not exist");
            return "";
        }

        String value = lang.get(key);

        if(value.equals("")){
            return "";
        }

        if(replace.length == 0){
            return value;
        }

        final int replaceArgsLength = (replace.length - (replace.length%2))/2;

        for(int index = 0; index < replaceArgsLength; index++){
            value = value.replace(replace[index], replace[index+replaceArgsLength]);
        }

        return value;
    }

    public final void log(Level level, String key, String... replace){
        final String message = getMessage(key, replace);
        if(message == ""){
            return;
        }

        logger.log(level, message);
    }

    public final void log(String key, String... replace){
        log(Level.INFO, key, replace);
    }

    public final boolean sendMessage(CommandSender sender, String key, String... replace){
        final String message = getMessage(key, replace);
        if(message == ""){
            return true;
        }

        sender.sendMessage(message);
        return true;
    }

    public final LanguageSession generateSession(CommandSender sender){
        return new LanguageSession(this, sender);
    }

    public final static class LanguageSession {

        private final Language language;
        private CommandSender sender;

        public LanguageSession(Language language, CommandSender sender){
            this.language = language;
            this.sender = sender;
        }

        public final boolean sendMessage(String key, String... replace){
            return language.sendMessage(sender, key, replace);
        }

        public Language getLanguage() {
            return language;
        }

        public CommandSender getSender() {
            return sender;
        }

        public void setSender(CommandSender sender) {
            this.sender = sender;
        }
    }

}
