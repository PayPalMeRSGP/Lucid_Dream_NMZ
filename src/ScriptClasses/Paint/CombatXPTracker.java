package ScriptClasses.Paint;

import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.API;

public class CombatXPTracker extends API{

    public enum CombatStyle {
        ATK, STR, DEF, CTRL, RNG
    }
    private CombatStyle lastStyle;
    private long startTime;
    private boolean moduleReady;

    @SuppressWarnings("deprecation")
    @Override
    public void initializeModule() {
        exchangeContext(getBot());
        startTime = System.currentTimeMillis();
        getExperienceTracker().start(Skill.HITPOINTS);

    }

    public void setModuleReady() {
        this.moduleReady = true;
    }

    //call in onloop, or onPaint
    public void setCombatStyle(){
        if(moduleReady){
            int s = getConfigs().get(43);
            String equippedWeapon = getEquipment().getItemInSlot(EquipmentSlot.WEAPON.slot).toString();
            boolean isRanging = equippedWeapon.contains("bow") || equippedWeapon.contains("blowpipe")
                    || equippedWeapon.contains("throwing") || equippedWeapon.contains("dart")
                    || equippedWeapon.contains("knife");
            if(isRanging){
                if(lastStyle != CombatStyle.RNG){
                    log("starting ranged tracker");
                    getExperienceTracker().start(Skill.RANGED);
                    lastStyle =  CombatStyle.RNG;
                }

                return;
            }

            switch (s){
                case 0:
                    if(lastStyle != CombatStyle.ATK){
                        log("starting attack tracker");
                        getExperienceTracker().start(Skill.ATTACK);
                        lastStyle = CombatStyle.ATK;
                    }
                    break;
                case 1:
                    if(lastStyle != CombatStyle.STR){
                        log("starting strength tracker");
                        getExperienceTracker().start(Skill.STRENGTH);
                        lastStyle = CombatStyle.STR;
                    }
                    break;
                case 2:
                    if(lastStyle != CombatStyle.CTRL){
                        log("starting controlled tracker");
                        getExperienceTracker().start(Skill.ATTACK);
                        getExperienceTracker().start(Skill.STRENGTH);
                        getExperienceTracker().start(Skill.DEFENCE);
                        lastStyle = CombatStyle.CTRL;
                    }

                    break;
                case 3:
                    if(lastStyle != CombatStyle.DEF){
                        log("starting defence tracker");
                        getExperienceTracker().start(Skill.DEFENCE);
                        lastStyle = CombatStyle.DEF;
                    }
                    break;
                default:
                    log("WARNING: hit default case in setCombatStyle switch statement");
            }
        }

    }

    public CombatStyle getLastStyle() {
        return lastStyle;
    }

    public long getRunTime(){
        return System.currentTimeMillis() - startTime;
    }

    //for not using controlled lastStyle
    public int getTrainingSkillLvl() {
        switch(lastStyle){
            case ATK:
                return getAtkLvl();
            case STR:
                return getStrLvl();
            case DEF:
                return getDefLvl();
            case RNG:
                return getRngLvl();
        }
        return 0;
    }

    public int getTrainingXpGained() {
        switch(lastStyle){
            case ATK:
                return getAtkXpGained();
            case STR:
                return getStrXpGained();
            case DEF:
                return getDefXpGained();
            case RNG:
                return getRngXpGained();
        }
        return 0;
    }

    public long getTrainingTTL() {
        switch(lastStyle){
            case ATK:
                return getAtkTTL();
            case STR:
                return getStrTTL();
            case DEF:
                return getDefTTL();
            case RNG:
                return getRngTTL();
        }
        return 0;
    }

    public int getTrainingXPH() {
        switch(lastStyle){
            case ATK:
                return getAtkXPH();
            case STR:
                return getStrXPH();
            case DEF:
                return getDefXPH();
            case RNG:
                return getRngXPH();
        }
        return 0;
    }

    //for using controlled lastStyle
    public int[] getAtkStrDefLvls(){
        return new int[]{getAtkLvl(), getStrLvl(), getDefLvl()};
    }

    //Levels
    public int getAtkLvl() {
        return getSkills().getStatic(Skill.ATTACK);
    }

    public int getStrLvl() {
        return getSkills().getStatic(Skill.STRENGTH);
    }

    private int getDefLvl() {
        return getSkills().getStatic(Skill.DEFENCE);
    }

    private int getRngLvl(){
        return getSkills().getStatic(Skill.RANGED);
    }

    public int getHpLvl(){
        return getSkills().getStatic(Skill.HITPOINTS);
    }

    //XP gained
    private int getAtkXpGained(){
        return getExperienceTracker().getGainedXP(Skill.ATTACK);
    }

    private int getStrXpGained(){
        return getExperienceTracker().getGainedXP(Skill.STRENGTH);
    }

    private int getDefXpGained(){
        return getExperienceTracker().getGainedXP(Skill.DEFENCE);
    }

    private int getRngXpGained(){
        return getExperienceTracker().getGainedXP(Skill.RANGED);
    }

    public int getHpXpGained(){
        return getExperienceTracker().getGainedXP(Skill.HITPOINTS);
    }

    //Time to Level
    public long getAtkTTL() {
        return getExperienceTracker().getTimeToLevel(Skill.ATTACK);
    }

    public long getStrTTL() {
        return getExperienceTracker().getTimeToLevel(Skill.STRENGTH);
    }

    public long getDefTTL() {
        return getExperienceTracker().getTimeToLevel(Skill.DEFENCE);
    }

    private long getRngTTL() {
        return getExperienceTracker().getTimeToLevel(Skill.RANGED);
    }

    public long getHpTTL() {
        return getExperienceTracker().getTimeToLevel(Skill.HITPOINTS);
    }

    //Xp per Hr
    private int getAtkXPH(){
        return getExperienceTracker().getGainedXPPerHour(Skill.ATTACK);
    }

    private int getStrXPH(){
        return getExperienceTracker().getGainedXPPerHour(Skill.STRENGTH);
    }

    private int getDefXPH(){
        return getExperienceTracker().getGainedXPPerHour(Skill.DEFENCE);
    }

    private int getRngXPH(){
        return getExperienceTracker().getGainedXPPerHour(Skill.RANGED);
    }

    public int getHpXPH(){
        return getExperienceTracker().getGainedXPPerHour(Skill.HITPOINTS);
    }

}
