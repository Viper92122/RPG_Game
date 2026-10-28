package io.github.echoesofnebula.entities;


public class Stats {
    private int maxHealth;
    private int currentHealth;
    private int maxMana;
    private int currentMana;
    private float moveSpeed;
    private int damage;

    private float howFarCanSee = 10f;

    public Stats(int maxHealth, int maxMana, float moveSpeed, int damage) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.maxMana = maxMana;
        this.currentMana = maxMana;
        this.moveSpeed = moveSpeed;
        this.damage = damage;
    }


    public float getRange(){
        return howFarCanSee;
    }
    public int getMaxHealth() { return maxHealth; }
    public int getCurrentHealth() { return currentHealth; }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.max(0, Math.min(currentHealth, maxHealth));
    }

    public void takeDamage(int amount) {
        this.currentHealth -= amount;
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public float getMoveSpeed() { return moveSpeed; }
    public int getDamage() { return damage; }

    public int getMaxMana() { return maxMana; }
    public int getCurrentMana() { return currentMana; }

    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(0, Math.min(currentMana, maxMana));
    }
}
