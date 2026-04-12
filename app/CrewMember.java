public class CrewMember {
    String name;
    String role;
    int baseSkill;
    int experience;

    public CrewMember(String name, String role, int baseSkill) {
        this.name = name;
        this.role = role;
        this.baseSkill = baseSkill;
        this.experience = 0;
    }

    public int getSkill() {
        return baseSkill + experience;
    }

    public boolean isScientist() {
        return role.equalsIgnoreCase("Scientist");
    }

    public void train(int bonus) {
        experience += 1 + bonus; // base + bonus
    }

    @Override
    public String toString() {
        return name + " (" + role + ") - XP: " + experience + " Skill: " + getSkill();
    }
}