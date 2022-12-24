package persistence;

import java.util.Objects;

public class HighScore {
    public final String name;
    public final String level;
    public final int score;

    public HighScore(String name, String level, int score) {
        this.name = name;
        this.level = level;
        this.score = score;
    }

    @Override
    public String toString() {
        return name + " " + level + " " + score;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.level);
        hash = 89 * hash + this.score;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HighScore other = (HighScore) obj;
        return this.score == other.score && 
                Objects.equals(this.level, other.level) && 
                Objects.equals(this.name, other.name);
    }
}
