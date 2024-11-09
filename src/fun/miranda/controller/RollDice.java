package fun.miranda.controller;

import fun.miranda.utils.Strings;
import fun.miranda.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fun.miranda.MeowCOC.plugin;

public class RollDice {
    private final Player PLPlayer;
    private final String PCPlayer;

    public RollDice(Player launcher, String action) {
        action = action.toLowerCase();
        this.PLPlayer = launcher;
        this.PCPlayer = this.getPCPlayer(this.PLPlayer.getName());
        String patternRDWithCheck = "^\\.r(\\d+)d(\\d+) (\\d+)$";
        String patternRD = "^\\.r(\\d+)d(\\d+)$";
        String patternRA = "^\\.ra (.+)$";
        String patternRH = "^\\.rh (.+) (.+)$";
        String patternSC = "^\\.sc (\\d+)d(\\d+)/(\\d+)d(\\d+)$";
        Matcher matchRDWithCheck = Pattern.compile(patternRDWithCheck).matcher(action);
        Matcher matchRD = Pattern.compile(patternRD).matcher(action);
        Matcher matchRA = Pattern.compile(patternRA).matcher(action);
        Matcher matchRH = Pattern.compile(patternRH).matcher(action);
        Matcher matchSC = Pattern.compile(patternSC).matcher(action);
        String message = "";
        if (matchRDWithCheck.find()) {
            Integer rollTime = Integer.parseInt(matchRDWithCheck.group(1));
            Integer dice = Integer.parseInt(matchRDWithCheck.group(2));
            Integer check = Integer.parseInt(matchRDWithCheck.group(3));
            message = this.RDWithCheck(rollTime, dice, check);
        } else if (matchRD.find()) {
            Integer rollTime = Integer.parseInt(matchRD.group(1));
            Integer dice = Integer.parseInt(matchRD.group(2));
            message = this.RD(rollTime, dice);
        } else if (matchRA.find()) {
            String key = matchRA.group(1);
            message = this.RA(key);
        } else if (matchRH.find()) {
            String target = matchRH.group(1);
            String key = matchRH.group(2);
            String broadcast;
            if (!Utils.allCards().contains(target)) {
                this.PLPlayer.sendMessage(Strings.CardNotFound);
                return;
            }
            if (Utils.isKP(this.PLPlayer)) {
                broadcast = String.format(Strings.RHShowKP, this.PLPlayer.getName());
            } else {
                broadcast = String.format(Strings.RHShowPlayer, this.PLPlayer.getName());
            }
            plugin.getServer().broadcastMessage(broadcast);
            message = Strings.EmptyResponse;
            this.RH(target, key);
        } else if (matchSC.find()) {
            Integer rollWhenSuccessR = Integer.parseInt(matchSC.group(1));
            Integer rollWhenSuccessD = Integer.parseInt(matchSC.group(2));
            Integer rollWhenFailR = Integer.parseInt(matchSC.group(3));
            Integer rollWhenFailD = Integer.parseInt(matchSC.group(4));
            message = this.SanCheck(rollWhenSuccessR, rollWhenSuccessD, rollWhenFailR, rollWhenFailD);
        }
        if (!Objects.equals(message, Strings.EmptyResponse)) {
            this.PLPlayer.sendMessage(message);
            plugin.log.log(message);
        }
    }


    private String RDWithCheck(Integer rollTime, Integer dice, Integer check) {
        if (rollTime <= 0 || dice <= 1 || check <= 1) {
            return Strings.EmptyResponse;
        }
        String checkResult;
        List<Integer> rollResult = this.roll(rollTime, dice);
        String input = String.format("r%dd%d", rollTime, dice);
        int sum = rollResult.stream().mapToInt(Integer::intValue).sum();
        String showPlayerName = this.PCPlayer == null ? this.PLPlayer.getName() : this.PCPlayer;
        if (rollTime == 1 && dice == 100) {
            checkResult = checkResultString(sum, check);
            return String.format(Strings.RDWithCheck1D, showPlayerName, input, check, sum, checkResult);
        } else {
            if (sum >= check) {
                checkResult = Strings.Fail;
            } else {
                checkResult = Strings.Success;
            }
            String rollResultString = String.join("+", rollResult.stream().map(String::valueOf).collect(Collectors.joining("+")));
            return String.format(Strings.RDWithCheck, showPlayerName, input, check, rollResultString, sum, checkResult);
        }
    }

    private String RD(Integer rollTime, Integer dice) {
        if (rollTime <= 0 || dice <= 1) {
            return Strings.EmptyResponse;
        }
        List<Integer> result = this.roll(rollTime, dice);
        String sumString = String.join("+", result.stream().map(String::valueOf).collect(Collectors.joining("+")));
        int sum = result.stream().mapToInt(Integer::intValue).sum();
        String input = String.format("r%dd%d", rollTime, dice);
        String showPlayerName = this.PCPlayer == null ? this.PLPlayer.getName() : this.PCPlayer;

        if (rollTime == 1) {
            return String.format(Strings.RD1D, showPlayerName, input, sum);
        } else {
            return String.format(Strings.RD, showPlayerName, input, sumString, sum);
        }
    }

    private String RA(String key) {
        if (this.PCPlayer == null) {
            return Strings.EmptyResponse;
        }
        PlayerCard card = new PlayerCard(this.PCPlayer);
        Integer result = card.getResult(key);
        if (result == null) {
            return String.format(Strings.KeyNotFound, key);
        }
        Integer rollResult = this.roll(1, 100).get(0);
        String checkResult = checkResultString(rollResult, result);
        return String.format(Strings.RA, this.PCPlayer, key, result, rollResult, checkResult);
    }

    private void RH(String target, String key) {
        PlayerCard card = new PlayerCard(target);
        Integer result = card.getResult(key);
        if (result == null) {
            this.PLPlayer.sendMessage(String.format(Strings.KeyNotFound, key));
            return;
        }
        ArrayList<Integer> rollResultList = this.roll(1, 100);
        Integer rollResult = rollResultList.get(0);
        String checkResult = checkResultString(rollResult, result);
        this.PLPlayer.sendMessage(String.format(Strings.RH, target, key, result, rollResult, checkResult));
        if (plugin.log != null) {
            plugin.log.log(String.format(Strings.RHLog, this.PLPlayer.getName(), target, key, result, rollResult, checkResult));
        }
    }

    private String SanCheck(Integer rollWhenSuccessR, Integer rollWhenSuccessD, Integer rollWhenFailR, Integer rollWhenFailD) {
        if (this.PCPlayer == null) {
            return Strings.EmptyResponse;
        }
        PlayerCard card = new PlayerCard(this.PCPlayer);
        Integer san = card.getResult(Strings.SAN);
        Integer rollResult = this.roll(1, 100).get(0);
        String rollCheck;
        String symptom;
        String rollResultString;
        int subSan;
        if (rollResult <= 5) {
            return String.format(Strings.SCBigSuccess, this.PCPlayer, rollResult);
        } else if (rollResult >= 96) {
            int subSanBigFail = rollWhenFailD * rollWhenFailR;
            if (subSanBigFail >= san) {
                symptom = Strings.CrazyForever;
            } else if (5 * subSanBigFail > san) {
                symptom = Strings.CrazyUncertainty;
            } else if (subSanBigFail > 5) {
                symptom = Strings.CrazyTemporary;
            } else {
                symptom = Strings.CrazyNo;
            }
            card.setWithSymbol(Strings.SAN, String.format("-%d", subSanBigFail));
            return String.format(Strings.SCBigFail, this.PCPlayer, rollResult, rollWhenFailD, symptom);
        } else if (rollResult > san) {
            ArrayList<Integer> rollWhenFail = this.roll(rollWhenFailR, rollWhenFailD);
            rollResultString = String.join("+", rollWhenFail.stream().map(String::valueOf).collect(Collectors.joining("+")));
            subSan = rollWhenFail.stream().mapToInt(Integer::intValue).sum();
            rollCheck = Strings.Fail;
        } else {
            ArrayList<Integer> rollWhenSuccess = this.roll(rollWhenSuccessR, rollWhenSuccessD);
            rollResultString = String.join("+", rollWhenSuccess.stream().map(String::valueOf).collect(Collectors.joining("+")));
            subSan = rollWhenSuccess.stream().mapToInt(Integer::intValue).sum();
            rollCheck = Strings.Success;
        }
        Integer CthulhuMyth = card.getResult(Strings.CthulhuMyth);
        int subSanFinal;
        boolean hasResist = false;
        if (san < CthulhuMyth) {
            subSanFinal = Math.floorDiv(subSan, 2);
            hasResist = true;
        } else {
            subSanFinal = subSan;
        }
        if (subSanFinal >= san) {
            symptom = Strings.CrazyForever;
        } else if (5 * subSanFinal > san) {
            symptom = Strings.CrazyUncertainty;
        } else if (subSanFinal >= 5) {
            symptom = Strings.CrazyTemporary;
        } else {
            symptom = Strings.CrazyNo;
        }
        Integer sanLeft = card.setWithSymbol(Strings.SAN, String.format("-%d", subSanFinal));
        if (hasResist) {
            return String.format(Strings.SCResultWithResist, this.PCPlayer, rollResult, san, rollCheck, rollResultString, subSan, subSanFinal, sanLeft, symptom);
        } else {
            return String.format(Strings.SCResult, this.PCPlayer, rollResult, san, rollCheck, rollResultString, subSanFinal, sanLeft, symptom);
        }
    }

    private Integer getRandomInteger(Integer range) {
        Random random = new Random();
        return random.nextInt(range) + 1;
    }

    private ArrayList<Integer> roll(Integer r, Integer d) {
        ArrayList<Integer> out = new ArrayList<>();
        for (int i = 0; i < r; i++) {
            int current = this.getRandomInteger(d);
            out.add(current);
        }
        return out;
    }

    private String getPCPlayer(String PLPlayer) {
        ConfigurationSection section = plugin.config.getConfigurationSection("COC.Players");
        if (section == null) {
            return null;
        } else {
            return section.getString(String.format("%s", PLPlayer), null);
        }
    }

    private String checkResultString(Integer sum, Integer check) {
        String checkResult;
        if (sum >= 96) {
            checkResult = Strings.BigFail;
        } else if (sum <= 5) {
            checkResult = Strings.BigSuccess;
        } else if (sum > check) {
            checkResult = Strings.Fail;
        } else {
            int hardToSuccess = check / 2;
            int veryHardToSuccess = check / 5;
            if (sum <= veryHardToSuccess) {
                checkResult = Strings.VeryHardSuccess;
            } else if (sum <= hardToSuccess) {
                checkResult = Strings.HardSuccess;
            } else {
                checkResult = Strings.Success;
            }
        }
        return checkResult;
    }
}
