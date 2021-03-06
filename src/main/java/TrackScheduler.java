import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

///////////  TrackScheduler takes an audioPlayer and guild and creates a queue that can start and stop tracks //////////////////////////////////
public class TrackScheduler extends AudioEventAdapter {
    ///////////  Variable instantiation for a TrackScheduler instance ////////////////////////////////////////////////////////////////////
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final Guild guild;

    ///////////  The TrackScheduler that the audioPlayer uses ///////////////////////////////////////////////////////////////////////////
    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guild = guild;
    }

    ///////////  Add the next track to queue or play right away if nothing is in the queue. @param track The track to play or add to queue. //////
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            System.out.println("Placed in queue");
        }
    }

    ///////////  Queues the next track if available in the queue ////////////////////////////////////////////////////////////////////////
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        if (!player.startTrack(queue.poll(), false)) {
            guild.getAudioManager().closeAudioConnection();
        }
    }

    ///////////  When a track finishes playing, queue the next track to play ////////////////////////////////////////////////////////////
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    ///////////  Clear the queue and stop all tracks ////////////////////////////////////////////////////////////////////////////////////
    public void StopAll() {
        queue.clear();
    }

    ///////////  Retrieve the length of the queue @ return The size of the queue. ///////////////////////////////////////////////////////
    public int getQueueLength() {
        return queue.size();
    }
}