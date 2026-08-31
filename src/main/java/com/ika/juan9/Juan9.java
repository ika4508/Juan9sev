package com.ika.juan9;

import org.bukkit.plugin.java.JavaPlugin;

public final class Juan9 extends JavaPlugin {

    private StatManager statManager;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.statManager = new StatManager(this);

        StatListener statListener = new StatListener(this, statManager);
        BlacksmithListener blacksmithListener = new BlacksmithListener(this);
        SpecialItemListener specialItemListener = new SpecialItemListener(this);

        // 수정된 부분
        MonsterExpListener monsterExpListener =
                new MonsterExpListener(statManager);

        MonsterHealthListener monsterHealthListener =
                new MonsterHealthListener();

        // 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(
                new RegionListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ShopClickListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ShopDamageListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CheckUseListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CashSecurityListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new BankGuiListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new FirstJoinListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                statListener,
                this
        );

        getServer().getPluginManager().registerEvents(
                blacksmithListener,
                this
        );

        getServer().getPluginManager().registerEvents(
                specialItemListener,
                this
        );

        getServer().getPluginManager().registerEvents(
                monsterExpListener,
                this
        );

        getServer().getPluginManager().registerEvents(
                monsterHealthListener,
                this
        );

        getServer().getPluginManager().registerEvents(
                new MonsterCombatListener(this),
                this
        );

        // 명령어 등록
        getCommand("setregion")
                .setExecutor(
                        new RegionCommand(this)
                );

        getCommand("spawnshop")
                .setExecutor(
                        new SpawnShopCommand()
                );

        getCommand("addtrade")
                .setExecutor(
                        new AddTradeCommand(this)
                );

        getCommand("deltrade")
                .setExecutor(
                        new DelTradeCommand(this)
                );

        getCommand("clearshop")
                .setExecutor(
                        new ClearShopCommand(this)
                );

        getCommand("money")
                .setExecutor(
                        new MoneyCommand(this)
                );

        getCommand("check")
                .setExecutor(
                        new CheckCommand()
                );

        getCommand("cash")
                .setExecutor(
                        new CashCommand(this)
                );

        getCommand("spawnbank")
                .setExecutor(
                        new SpawnBankCommand()
                );

        getCommand("stat")
                .setExecutor(
                        new StatCommand(
                                this,
                                statManager,
                                statListener
                        )
                );

        getCommand("spawnblacksmith")
                .setExecutor(
                        new SpawnBlacksmithCommand()
                );

        getCommand("upgrade")
                .setExecutor(
                        new UpgradeCommand(
                                blacksmithListener
                        )
                );

        getCommand("rpgitem")
                .setExecutor(
                        new RpgItemCommand(
                                specialItemListener
                        )
                );

        getCommand("setcenter")
                .setExecutor(
                        new SetCenterCommand(this)
                );

        getCommand("killnpc")
                .setExecutor(
                        new KillNpcCommand()
                );

        getCommand("invsee")
                .setExecutor(
                        new InvseeCommand()
                );

        getCommand("mobdamage")
                .setExecutor(
                        new MonsterDamageCommand(this)
                );

        getLogger().info(
                "Juan9 플러그인이 성공적으로 활성화되었습니다!"
        );
    }

    @Override
    public void onDisable() {

        getLogger().info(
                "Juan9 플러그인이 비활성화되었습니다."
        );
    }

    public StatManager getStatManager() {
        return statManager;
    }
}
