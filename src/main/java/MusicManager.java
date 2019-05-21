import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;



public class MusicManager extends AudioEventAdapter {

    public static void PlayMusic(String identifier, Guild guild, VoiceChannel channel) {

        AudioPlayerManager manager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(manager);

        AudioPlayer player = manager.createPlayer();
        manager.setFrameBufferDuration(1000);
        TrackScheduler scheduler = new TrackScheduler(player, guild);

        player.addListener(scheduler);

        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));

        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                scheduler.queue(track);
                System.out.println("Scheduled track : " + track.getInfo().title);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {
                // Notify the user that we've got nothing
                System.out.println("No matches for track selected");
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                // Notify the user that everything exploded
                System.out.println("Error playing track : " + throwable);
            }
        });

        //System.out.println("Playing track : " + track.getInfo().title);
        //System.out.println("Volume : " + player.getVolume());
        guild.getAudioManager().openAudioConnection(channel);
    }

    public static void StopMusic(Guild guild){
        guild.getAudioManager().closeAudioConnection();
    }

}
