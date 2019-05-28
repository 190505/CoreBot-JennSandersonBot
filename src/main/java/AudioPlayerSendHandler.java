import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;

///////////  AudioPlayerSendHandler takes the audioPlayer instance and a loaded track and formats the audio data into a playable format //////////////////////////////////
public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    ///////////  Method for assigning the audioPlayer to the instance of AudioPlayerSendHandler ///////////////////////////////////////////////////////////////////////////
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    ///////////  Updates the data in the audioplayer //////////////////////////////////
    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    ///////////  Formats the audio data into a byte buffer for playback ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        ByteBuffer buffer = ByteBuffer.wrap(lastFrame.getData());
        return buffer;
    }

    ///////////  Default method of AudioSendHandler ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean isOpus() {
        return true;
    }
}