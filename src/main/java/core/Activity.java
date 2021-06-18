package core;


/**
 * All classes that answer activity of the body by representing
 * it must implement this interface.
 */
public interface Activity {

    /**
     * Whenever this method is invoked, the calling-class should own the bodyLayer
     * in its own way, with possible pre- and, or post-tasks.
     * E.g: the {@link core.module.Analysis} type answers by making pre-tasks;
     * while the {@link core.transcript.TranscriptActivity}, post-tasks.
     *
     * The consequence of this must not only be the result of a home-panel click,
     * as it traditionally use to be.
     * By convention, this call should physically be placed directly beneath the constructor
     * of such types.
     *
     * @see Board
     */
    void answerActivity();

}
