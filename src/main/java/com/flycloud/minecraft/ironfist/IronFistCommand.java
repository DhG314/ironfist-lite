package com.flycloud.minecraft.ironfist;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
                                        .executes(new setLVCommand()))));
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
}
