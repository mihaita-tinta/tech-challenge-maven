package com.tech.challenge.maven.kafka.events;

// This class is generated by the jsonCodeGen bean template (bean.txt), changes have to implement there


import lombok.ToString;

/**
 *
 *     "gameId": {
 *       "description": "ID of the game",
 *       "type": "string"
 *     },
 *     "tournamentId": {
 *       "description": "ID of the tournament",
 *       "type": "string"
 *     },
 *     "battlegroundSize": {
 *       "description": "The size of the battleground - a matrix of battlegroundSize x battlegroundSize",
 *       "type": "number"
 *     },
 *     "battleshipTemplate":
 */
@ToString
public class GameStarted {
    
    private String gameId;

    public String getGameId () { return this.gameId; }

    public void setGameId (String gameId) {
        this.gameId = gameId;

    }

    private String tournamentId;

    public String getTournamentId () { return this.tournamentId; }

    public void setTournamentId (String tournamentId) {
        this.tournamentId = tournamentId;

    }
    private int battlegroundSize;

    public int getBattlegroundSize() {
        return battlegroundSize;
    }

    public void setBattlegroundSize(int battlegroundSize) {
        this.battlegroundSize = battlegroundSize;
    }

    private BattleshipTemplate battleshipTemplate;

    public BattleshipTemplate getBattleshipTemplate() {
        return battleshipTemplate;
    }

    public void setBattleshipTemplate(BattleshipTemplate battleshipTemplate) {
        this.battleshipTemplate = battleshipTemplate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if ( ! (obj instanceof GameStarted)) return false;

        GameStarted _typeInst = (GameStarted) obj;
       // handling of non-array-types
    
        String _gameId = _typeInst.getGameId ();
        if (this.gameId == null && _gameId != null) return false;
        if (this.gameId != null) {
            if (!this.gameId.equals(_gameId)) return false;
        }
    
        String _tournamentId = _typeInst.getTournamentId ();
        if (this.tournamentId == null && _tournamentId != null) return false;
        if (this.tournamentId != null) {
            if (!this.tournamentId.equals(_tournamentId)) return false;
        }
    
       // handling of array-types
    
        return true;
    }

}

