var gamesVue = new Vue({
    el:"#app",
    data:{
        title:"Games Salvo Created",
        games:[],
        userNames:[],
        username:null,
        password:null,
        player:null
    },

    methods: {
        reloadurl : function (url){
            location.href=url;
        },

        joinGame : function(gameId){
            if(gamesVue.player != null){
                $.post("/api/game/" + gameId + "/players")
                    .done(function(responseData){
                        location.href = '/web/game.html?gp=' + responseData.gpId;
                        alert("Game joined")
                    })
                    .fail(function(){
                        alert("Cant join  game")
                     })
            }else{
                alert("Not user found")
            }
        },

        createGame : function(){
            if(gamesVue.player != null){
               $.post("/api/games")
                    .done(function(responseData){
                        alert("Game created " + responseData.gpId);
                        location.href = '/web/game.html?gp=' + responseData.gpId;
               })
               .fail(function(){
                    alert("Cant create  game")
               })
            }else{
                alert("No user found")
            }
        },

        login: function(){
            if((gamesVue.username && gamesVue.password) && (gamesVue.username.length !=0 || gamesVue.password.length !=0 )){
                $.post("/api/login", { username: gamesVue.username, password: gamesVue.password })
                .done(function() { location.reload(); })
                .fail(function() { alert("No user found") })
            }else{
                alert("You must enter the data")
            }
        },

        logout: function(){
            $.post("/api/logout").done(function() {location.reload()})
        },
        //Detalle x sacado del salvocontroller funcion register
        register: function(){
                    if((gamesVue.username && gamesVue.password) && (gamesVue.username.length !=0 || gamesVue.password.length !=0 )){
                        $.post("/api/players", { username: gamesVue.username, password: gamesVue.password })
                        .done(function() {gamesVue.login()})
                        .fail(function(x){alert(x.responseText)})
                    }else{
                        alert("You must enter the data")
                    }
                },

        getPlayersScore: function(){
            for(var i = 0; i < gamesVue.games.length; i++) {
                for(var j = 0; j < gamesVue.games[i].gamePlayers.length; j++) {
                    if(gamesVue.userNames.find(x => x.player == gamesVue.games[i].gamePlayers[j].userName) == null) {
                        var userName = gamesVue.games[i].gamePlayers[j].userName;
                        gamesVue.userNames.push(
                            {
                                player: userName,
                                total: gamesVue.getTotalScores(userName),
                                win: gamesVue.getWins(userName),
                                lose: gamesVue.getLoses(userName),
                                draw: gamesVue.getDraws(userName),
                            }
                        );
                    }
                }
            }
        },
        getTotalScores: function(paramMail){
            var totalScore = 0;
            for(var i = 0; i < gamesVue.games.length; i++) {
                for(var j = 0; j < gamesVue.games[i].gamePlayers.length; j++) {
                     if(gamesVue.games[i].gamePlayers[j].userName == paramMail)
                        totalScore  += gamesVue.games[i].gamePlayers[j].score;

                }
            }
            return totalScore;
        },
        getWins: function(paramMail){
            var totalWin = 0;
            for(var i = 0; i < gamesVue.games.length; i++) {
                for(var j = 0; j < gamesVue.games[i].gamePlayers.length; j++) {
                     if(gamesVue.games[i].gamePlayers[j].userName == paramMail)
                        if(gamesVue.games[i].gamePlayers[j].score == 1)
                            totalWin++;

                }
            }
            return totalWin;
        },
        //Otra forma de lograr lo mismo mas prolijo seria
        /*if(gamesVue.games[i].gamePlayers[j].userName == paramMail && gamesVue.games[i].gamePlayers[j].score == 1)
          totalWins+=1;*/
        getLoses: function(paramMail){
             var totalLose = 0;
             for(var i = 0; i < gamesVue.games.length; i++) {
                 for(var j = 0; j < gamesVue.games[i].gamePlayers.length; j++) {
                      if(gamesVue.games[i].gamePlayers[j].userName == paramMail)
                         if(gamesVue.games[i].gamePlayers[j].score == 0)
                            totalLose++;

                 }
             }
             return totalLose;
        },
        getDraws: function(paramMail){
             var totalDraw = 0;
             for(var i = 0; i < gamesVue.games.length; i++) {
                 for(var j = 0; j < gamesVue.games[i].gamePlayers.length; j++) {
                      if(gamesVue.games[i].gamePlayers[j].userName == paramMail)
                         if(gamesVue.games[i].gamePlayers[j].score == 0.5)
                            totalDraw++;

                 }
             }
             return totalDraw;
        }
    }
});


fetch('/api/games')
.then(function(response){
return response.json()
})
.then(function(json){
    gamesVue.games = json.games;
    gamesVue.player = json.player;
    gamesVue.getPlayersScore();
    //console.log(gamesVue.getTotalScores("j.bauer@ctu.gov"));
    //console.log(gamesVue.getWins("j.bauer@ctu.gov"));
    //console.log(gamesVue.getLoses("c.obrian@ctu.gov"));
    //console.log(gamesVue.getDraws("t.almeida@ctu.gov"));

});

