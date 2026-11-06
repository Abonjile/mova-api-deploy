const initialReward = {
    userId: 'usr_default_test',
    streakDays: 1,
    consistencyLevel: 'Starter',
    claimableRewards: [
        {
            id: 'reward_first_step',
            name: 'First Step Badge', // Matches @SerializedName("name")
            description: 'Unlocked immediately upon completing your first introductory goal.', // Matches @SerializedName("description")
            isUnlocked: true,
            isClaimed: false,
            iconUrl: 'ic_number_one_trophy' // Matches @SerializedName("iconUrl")
        }
    ],
    milestoneBadges: [
        { 
            id: 'badge_workout_1',
            name: 'First Workout', 
            description: 'Completed session 1', 
            isUnlocked: true,
            isClaimed: true,
            iconUrl: 'ic_red_medal' 
        },
        { 
            id: 'badge_calorie_1',
            name: 'Calorie Crusher', 
            description: 'Burned 100+ kcal', 
            isUnlocked: true, 
            isClaimed: false,
            iconUrl: 'ic_golden_medal' 
        },
        { 
            id: 'badge_consistency_1',
            name: 'Consistency Pro', 
            description: '7-Day Streak', 
            isUnlocked: false, 
            isClaimed: false,
            iconUrl: 'ic_golden_trophy' 
        }
    ]
};