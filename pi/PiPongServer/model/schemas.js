schemas = {  
    player: {
        id: null,
        name: null,
        timestamp: null,
        user: null
    },
    game: {
    	id: null,
    	type: null,
    	date: null,
    	player1_name: null,
    	player2_name: null,
    	player1_score: null,
    	player2_score: null,
        timestamp: null,
        user: null
    },
    user: {
        id: null,
        hash: null
    }
}

module.exports = schemas;  