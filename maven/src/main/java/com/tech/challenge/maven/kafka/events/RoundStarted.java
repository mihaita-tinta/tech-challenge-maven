package com.tech.challenge.maven.kafka.events;

// This class is generated by the jsonCodeGen bean template (bean.txt), changes have to implement there


import lombok.ToString;

/**
 * Schema to describe a started round.
 */
@ToString
public class RoundStarted {
    
    
    private String gameId;
    
    public String getGameId () { return this.gameId; }

    public void setGameId (String gameId) {
        this.gameId = gameId;
        
    }

    
    private Integer roundNo;
    
    public Integer getRoundNo () { return this.roundNo; }

    public void setRoundNo (Integer roundNo) {
        this.roundNo = roundNo;
        
    }

    
    private String tournamentId;
    
    public String getTournamentId () { return this.tournamentId; }

    public void setTournamentId (String tournamentId) {
        this.tournamentId = tournamentId;
        
    }

    

    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        if ( ! (obj instanceof RoundStarted)) return false;

        RoundStarted _typeInst = (RoundStarted) obj;
       // handling of non-array-types
    
        String _gameId = _typeInst.getGameId ();
        if (this.gameId == null && _gameId != null) return false;
        if (this.gameId != null) {
            if (!this.gameId.equals(_gameId)) return false;
        }
    
        Integer _roundNo = _typeInst.getRoundNo ();
        if (this.roundNo == null && _roundNo != null) return false;
        if (this.roundNo != null) {
            if (!this.roundNo.equals(_roundNo)) return false;
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

