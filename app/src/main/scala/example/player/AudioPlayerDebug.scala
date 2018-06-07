package example.player

class AudioPlayerDebug(audioPlayer: AudioPlayer) {
  import audioPlayer.{data => $}
  Seq(
    "duration" -> $.duration,
    "position" -> $.position,
    "progress" -> $.progress,
    "isPlaying" -> $.isPlaying,
    "song" -> $.song,
    "songIndex" -> $.songIndex,
    "playlist" -> $.playlist
  ).map {
    case (n, s) => s.subscribe(v => println("player.streams." + n +":" +  v))
  }
}
