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
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;


///////////  MusicManager handles the majority of scheduling tracks and usage of the lavaplayer library /////////////////////////////////////
public class MusicManager extends AudioEventAdapter {

    ///////////  PlayMusic method - creates a DefaultAudioPlayerManager and registers it with music sources(Soundcloud, Youtube, etc). Creates an audioPlayer, and a TrackScheduler //////////
    public static void PlayMusic(String identifier, Guild guild, VoiceChannel channel, MessageChannel textChannel) {

///////////  Instantiate a new DefaultAudioPlayerManager and register //////////////////////////////////////////////////////////////////////
        AudioPlayerManager manager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(manager);
///////////  Instantiate a new audioPlayer and assign the buffer size. ////////////////////////////////////////////////////////////////////
        AudioPlayer player = manager.createPlayer();
        manager.setFrameBufferDuration(1000);
///////////  Instantiates a new TrackScheduler and adds a listener /////////////////////////////////////////////////////////////////////////
        TrackScheduler scheduler = new TrackScheduler(player, guild);
        player.addListener(scheduler);
///////////  Set the audio manager of the guild to the new AudioPlayerSendHandler ///////////////////////////////////////////////////////////
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
///////////  Synchronous song loading with a track identifier, passing the guild as well ///////////////////////////////////////////////////
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            ///////////  Successfully loaded one track, notify channel /////////////////////////////////////////////////////////////////////
            @Override
            public void trackLoaded(AudioTrack track) {
                scheduler.queue(track);
                textChannel.sendMessage("Scheduled track : " + track.getInfo().title).queue();
                textChannel.sendMessage("Volume : " + player.getVolume()).queue();
            }

            ///////////  Successfully loaded playlist and add to queue  /////////////////////////////////////////////////////////////////////////
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                textChannel.sendMessage("Queued playlist " + playlist).queue();
            }

            ///////////  No matches for track, notify and close audio connection ////////////////////////////////////////////////////////////////
            @Override
            public void noMatches() {
                // Notify the user that we've got nothing
                textChannel.sendMessage("No matches for track selected").queue();
                guild.getAudioManager().closeAudioConnection();
            }

            ///////////  Load failed, notify in chat and close audio connection //////////////////////////////////////////////////////////////////
            @Override
            public void loadFailed(FriendlyException throwable) {
                // Notify the user that everything exploded
                textChannel.sendMessage("Error playing track " + throwable).queue();
                guild.getAudioManager().closeAudioConnection();
            }
        });
        guild.getAudioManager().openAudioConnection(channel);
    }
    ///////////  method StopMusic closes the audio connection ////////////////////////////////////////////////////////////////////////////////////
    public static void StopMusic(Guild guild){
        guild.getAudioManager().closeAudioConnection();
    }



}

