package com.flycloud.minecraft.ironfist;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = IronFist.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IronFistCommand {
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(
                Commands.literal(IronFist.MODID)
                        .then(Commands.literal("showLV")
                                .executes(new showLVCommand()))
                        .then(Commands.literal("setLV").requires(p -> p.hasPermission(2))
                                .then(Commands.argument("lv", IntegerArgumentType.integer(1,Config.maxLV))
                                        .executes(new setLVCommand())))
                        .then(Commands.literal("config")
                                .then(Commands.literal("fistOnly")
                                        .executes(showConfig("fistOnly", () -> Config.fistOnly))
                                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                                .executes(new changeConfigCommand.FistOnly())))
                                .then(Commands.literal("maxLV")
                                        .executes(showConfig("maxLV", () -> Config.maxLV))
                                        .then(Commands.argument("int", IntegerArgumentType.integer(0))
                                                .executes(new changeConfigCommand.MaxLV())))
                                .then(Commands.literal("XPMultiple")
                                        .executes(showConfig("XPMultiple", () -> Config.XPMultiple))
                                        .then(Commands.argument("float", FloatArgumentType.floatArg(0.1f))
                                                .executes(new changeConfigCommand.XPMultiple())))
                                .then(Commands.literal("limitBreakSpeed")
                                        .executes(showConfig("limitBreakSpeed", () -> Config.limitBreakSpeed))
                                        .then(Commands.argument("int", IntegerArgumentType.integer(0))
                                                .executes(new changeConfigCommand.LimitBreakSpeed())))
                                .then(Commands.literal("fistDamage")
                                        .executes(showConfig("fistDamage", () -> Config.fistDamage))
                                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                                .executes(new changeConfigCommand.FistDamage())))
                                .then(Commands.literal("fistRange")
                                        .executes(showConfig("fistRange", () -> Config.fistRange))
                                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                                .executes(new changeConfigCommand.FistRange())))
                                .then(Commands.literal("saveDataOnDeath")
                                        .executes(showConfig("saveDataOnDeath", () -> Config.saveDataOnDeath))
                                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                                .executes(new changeConfigCommand.SaveDataOnDeath())))
                        )
        );
    }

    private static Command<CommandSourceStack> showConfig(String label, Supplier<Object> valueSupplier) {
        return ctx -> {
            if (ctx.getSource().getEntity() instanceof Player player) {
                IronFistPlayer.get(player).say("config " + label + " = " + valueSupplier.get());
            }
            return 1;
        };
    }

    public static class showLVCommand implements Command<CommandSourceStack> {

        @Override
        public int run(CommandContext<CommandSourceStack> commandContext) {
            if (commandContext.getSource().getEntity() instanceof Player player) {
                IronFistPlayer IFPlayer = IronFistPlayer.get(player);
                IFPlayer.say(StringUtils.translateWithFormat("fist.showdata", IFPlayer.getFistLV(), IFPlayer.getFistXP(), IFPlayer.getRequiredXP()));
            }
            return 1;
        }
    }
    public static class setLVCommand implements Command<CommandSourceStack>{

        @Override
        public int run(CommandContext<CommandSourceStack> commandContext) {
            if (commandContext.getSource().getEntity() instanceof Player player) {
                IronFistPlayer IFPlayer = IronFistPlayer.get(player);
                int lv = commandContext.getArgument("lv", Integer.class);
                IFPlayer.setFistLV(lv);
                IFPlayer.say("set lv to"+ lv);
            }
            return 1;
        }
    }
    public static class changeConfigCommand{
        public static class FistOnly implements Command<CommandSourceStack> {
            @Override
            public int run(CommandContext<CommandSourceStack> commandContext) {
                if (commandContext.getSource().getEntity() instanceof Player player) {
                    Config.fistOnly = commandContext.getArgument("bool", Boolean.class);
                    IronFistPlayer.get(player).say("config fistOnly set to " + Config.fistOnly);
                    Config.save();
                }
                return 1;
            }
        }
        public static class MaxLV implements Command<CommandSourceStack> {
            @Override
            public int run(CommandContext<CommandSourceStack> commandContext) {
                if (commandContext.getSource().getEntity() instanceof Player player) {
                    Config.maxLV = commandContext.getArgument("int", Integer.class);
                    IronFistPlayer.get(player).say("config maxLV set to " + Config.maxLV);
                    Config.save();
                }
                return 1;
            }
        }
        public static class XPMultiple implements Command<CommandSourceStack> {
            @Override
            public int run(CommandContext<CommandSourceStack> commandContext) {
                if (commandContext.getSource().getEntity() instanceof Player player) {
                    Config.XPMultiple = commandContext.getArgument("float", Float.class);
                    IronFistPlayer.get(player).say("config XPMultiple set to " + Config.XPMultiple);
                    Config.save();
                }
                return 1;
            }
        }
        public static class LimitBreakSpeed implements Command<CommandSourceStack> {
            @Override
            public int run(CommandContext<CommandSourceStack> commandContext) {
                if (commandContext.getSource().getEntity() instanceof Player player) {
                    Config.limitBreakSpeed = commandContext.getArgument("int", Integer.class);
                    IronFistPlayer.get(player).say("config limitBreakSpeed set to " + Config.limitBreakSpeed);
                    Config.save();
                }
                return 1;
            }
        }
        public static class FistDamage implements Command<CommandSourceStack> {
            @Override
            public int run(CommandContext<CommandSourceStack> commandContext) {
                if (commandContext.getSource().getEntity() instanceof Player player) {
                    Config.fistDamage = commandContext.getArgument("bool", Boolean.class);
                    IronFistPlayer.get(player).say("config fistDamage set to " + Config.fistDamage);
                    Config.save();
                    IronFistPlayer.get(player).applyAllModifiers();
                }
                return 1;
            }
        }
        public static class FistRange implements Command<CommandSourceStack> {
            @Override
            public int run(CommandContext<CommandSourceStack> commandContext) {
                if (commandContext.getSource().getEntity() instanceof Player player) {
                    Config.fistRange = commandContext.getArgument("bool", Boolean.class);
                    IronFistPlayer.get(player).say("config fistRange set to " + Config.fistRange);
                    Config.save();
                    IronFistPlayer.get(player).applyAllModifiers();
                }
                return 1;
            }
        }
        public static class SaveDataOnDeath implements Command<CommandSourceStack> {
            @Override
            public int run(CommandContext<CommandSourceStack> commandContext) {
                if (commandContext.getSource().getEntity() instanceof Player player) {
                    Config.saveDataOnDeath = commandContext.getArgument("bool", Boolean.class);
                    IronFistPlayer.get(player).say("config saveDataOnDeath set to " + Config.saveDataOnDeath);
                    Config.save();
                }
                return 1;
            }
        }
    }
}
