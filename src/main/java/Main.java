import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.push.DeviceRegistrationResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import javax.sound.midi.Track;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

///////////  Main - runs Discord bot, lavaplayer, server communication ///////////////////////////////////////////////////////////////////////////////
public class Main extends ListenerAdapter {

    ///////////  Variable instantiation for server communications, Discord utilities, and message strings //////////////////////////////////////////////
    private String identifier;
    private String message;
    private Guild guild;
    private VoiceChannel voiceChannel;
    private MessageChannel textChannel;
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static InputStreamReader inputStreamReader;
    private static String serverMessage;
    private static String serverLink;
    private static String serverChat;

    ///////////  psvm links program up to discord using client_token and instantiates a new bot, server communications //////////////////////////////////
    public static void main(String[] args) throws LoginException {
        ///////////  Instantiate a bot using client_token //////////////////////////////////
        String CLIENT_TOKEN = "NTczNTA2MjA0MzkxODMzNjEw.XNA6JQ.gV5V0msJ3ewecvZoPtuxpR88b64";
        JDA bot = new JDABuilder(CLIENT_TOKEN).build();
        bot.addEventListener(new Main());

        ///////////  Hosts a server that listens for messages over port 5000 //////////////////////////////////////////////////////////////////////////
        System.out.println("SERVER RUNNING ON PORT 5000");
        try {
            while (true) {
                serverSocket = new ServerSocket(5000);
                socket = serverSocket.accept();
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                serverMessage = bufferedReader.readLine();
         ///////  Message modification to strings to determine which messages are song queue or "Talk with Jenn" ///////////////////////////////////////////
                if (serverMessage.startsWith("+++")){
                    serverLink = serverMessage.substring(3);
                }
                else if (serverMessage.startsWith("---")){
                    serverChat = serverMessage.substring(3);
                }
                System.out.println(serverMessage);
                inputStreamReader.close();
                bufferedReader.close();
                serverSocket.close();
                socket.close();
            }
        }
        ///////////  Catches exceptions due to failed connections //////////////////////////////////////////////////////////////////////////////////////////////
        catch (IOException exception){
            exception.printStackTrace();
        }
    }

    ///////////  onMessageRecieved tracks all 'events' from all guilds that the bot has been invited to /////////////////////////////////////////////////////
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        ///////////  Conditionals for preventing infinite loops and variable assignments ///////////////////////////////////////////////////////////////////
        if (!event.getAuthor().isBot()) {
            System.out.println(event.getAuthor().getName() + " : " + event.getMessage().getContentDisplay());
            guild = event.getGuild();
            voiceChannel = event.getMember().getVoiceState().getChannel();
            message = event.getMessage().getContentDisplay();
            textChannel = event.getChannel();
        ///////////  Command Jenn play which pulls a provided link and calls the PlayMusic method ///////////////////////////////////////////////////////////
            if (event.getMessage().getContentDisplay().startsWith("Jenn play")) {
                if (event.getMember().getVoiceState().inVoiceChannel()) {
                    identifier = message.substring(message.indexOf(" ") + 1);
                    MusicManager.PlayMusic(identifier, guild, voiceChannel, textChannel);
                }
                else{
                    textChannel.sendMessage("Who do you think you think you're talking to?! Join a voice channel to play music").queue();
                }
            }
            ///////////  Command Jenn phone pulls a link from the app and calls the PlayMusic method //////////////////////////////////////////////////////////
            if (event.getMessage().getContentDisplay().startsWith("Jenn phone")) {
                if (event.getMember().getVoiceState().inVoiceChannel()) {
                    textChannel.sendMessage("Playing " + serverLink).queue();
                    MusicManager.PlayMusic(serverLink, guild, voiceChannel, textChannel);
                }
                else{
                    textChannel.sendMessage("You cant play music from the app without joining a voice channel first").queue();
                }
            }
            ///////////  Command Jenn stop stops all music with the StopMusic method ///////////////////////////////////////////////////////////////////////////
            if (event.getMessage().getContentDisplay().startsWith("Jenn stop")) {
                MusicManager.StopMusic(guild);
            }
            ///////////  Command Jenn Help displays commands in the chat //////////////////////////////////////////////////////////////////////////////////////
            if (event.getMessage().getContentDisplay().startsWith("Jenn help")) {
                textChannel.sendMessage("Play music with command 'Jenn play [url]'").queue();
                textChannel.sendMessage("Stop music with command 'Jenn stop'").queue();
                textChannel.sendMessage("Queue music through the app by sending the queue first, then typing 'Jenn phone'").queue();
                textChannel.sendMessage("Talk with Jenn with command 'Jenn Talk'").queue();
            }
            ///////////  Command Jenn talk pulls data from the app and reads it out in chat ////////////////////////////////////////////////////////////////////
            if (event.getMessage().getContentDisplay().startsWith("Jenn talk")) {
                textChannel.sendMessage(serverChat).queue();
            }
        }
    }






}
