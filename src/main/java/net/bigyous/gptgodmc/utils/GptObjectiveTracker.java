package net.bigyous.gptgodmc.utils;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Score;

public class GptObjectiveTracker implements Runnable {

    private Score score;

    private int taskId;

    public GptObjectiveTracker(Score score){
        this.score = score;
    }

    public void setTaskId(int id){
        this.taskId = id;
    }

    @Override
    public void run() {
        if (score.getScore() < 1){
            score.resetScore();
            Bukkit.getScheduler().cancelTask(taskId);
        } else {
            score.setScore(score.getScore() - 1);
        }
    }
    
}
