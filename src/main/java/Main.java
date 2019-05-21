import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class Main extends ListenerAdapter {

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



    public static void main(String[] args) throws LoginException {
        String CLIENT_TOKEN = "NTczNTA2MjA0MzkxODMzNjEw.XNA6JQ.gV5V0msJ3ewecvZoPtuxpR88b64";
        JDA bot = new JDABuilder(CLIENT_TOKEN).build();
        bot.addEventListener(new Main());

        try {
            while (true) {
                serverSocket = new ServerSocket(5000);
                System.out.println("WEBSERVER RUNNING ON PORT 5000");
                socket = serverSocket.accept();
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                serverMessage = bufferedReader.readLine();
                System.out.println("WEBSERVER :: " + serverMessage);
                inputStreamReader.close();
                bufferedReader.close();
                serverSocket.close();
                socket.close();
            }
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            System.out.println(event.getAuthor().getName() + " : " + event.getMessage().getContentDisplay());
            guild = event.getGuild();
            voiceChannel = event.getMember().getVoiceState().getChannel();
            message = event.getMessage().getContentDisplay();
            textChannel = event.getChannel();

            if (event.getMessage().getContentDisplay().startsWith("Jenn play")) {
                if (event.getMember().getVoiceState().inVoiceChannel()) {
                    identifier = message.substring(message.indexOf(" ") + 1);
                    MusicManager.PlayMusic(identifier, guild, voiceChannel);
                }
                else{
                    textChannel.sendMessage("Who do you think you think you're talking to?! Join a voice channel to play music").queue();
                }
            }
            if (event.getMessage().getContentDisplay().startsWith("Jenn stop")) {
                MusicManager.StopMusic(guild);
            }
            if (event.getMessage().getContentDisplay().startsWith("Jenn help")) {

                textChannel.sendMessage("Play music with command 'Jenn play url'").queue();
                textChannel.sendMessage("Stop music with command 'Jenn stop'").queue();
            }
        }
    }
}
