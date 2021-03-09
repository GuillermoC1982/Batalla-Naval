
var gamesVue = new Vue({
    el:"#app",
    data:{
        columns:[1,2,3,4,5,6,7,8,9,10],
        rows:["A","B","C","D","E","F","G","H","I","J"],
        gameView: { ships: [] },
        orientationSel: null,
        shipSel: {},
        status:"none",
        players:{
            viewer: null,
            enemy: null,
            },
        shipTypes:[ { type: "Destroyer", length: 3, canMax: 1, disabled: false, },
                    { type: "Patrol Boat", length: 2, canMax: 1, disabled: false },
                    { type: "Submarine", length: 3, canMax: 1, disabled: false },
                    { type: "Battleship", length: 4, canMax: 1, disabled: false },
                    { type: "Carrier", length: 5, canMax: 1, disabled: false }
                  ],
        shipDefineds:[],
        shipSinks : [],
        turnoSalvoLocations: {
            turn : 0,
            salvoLocations : []
        },
    },
    methods: {

        getStatus: function(){

        switch(gamesVue.gameView.status){
            case "PLACE_SHIPS":
               gamesVue.status = "Place your ships and prepare for battle!!";
            break;
            case "WAITING_OPONENT":
               gamesVue.status = "Waiting for a worthy rival";
            break;
            case "WAIT_OPONENT_PLACING_SHIPS":
               gamesVue.status = "Wait! your oponent is placing his ships..";
            break;
            case "FIRE":
               gamesVue.status = "Open Fire!!";
            break;
            case "WAIT":
               gamesVue.status = "Wait... an attack is coming";
            break;
            case "WIN":
               gamesVue.status = "Congratulations!! You Win!! it's a glorious day!!";
            break;
            case "LOSE":
               gamesVue.status = "You Lose!! You are the shame of the nation..";
            break;
            case "TIE":
               gamesVue.status = "It's a Tie.. better luck next time";
            break;
            default:
               gamesVue.status = "There is no state";


        }

        },

        getNextTurno: function(gpid){

            var nextTurn = 0;
            var misTurnos = gamesVue.gameView.salvos.filter(x => x.idGamePlayer == gpid);
            return misTurnos.length + 1;
              
        },

        placeSalvos: function(letra, numero){

            var salvoL = "S" + letra + numero;
            if(document.getElementById(salvoL).classList.contains('salvos') || document.getElementById(salvoL).classList.contains('hit'))
            {
                alert('Already shot in this position');
                return;
            }

            var salvoExistente = gamesVue.turnoSalvoLocations.salvoLocations.findIndex(x => x == letra + numero);
            if(salvoExistente != -1) //encontro???
            {
                //borro tiro
                gamesVue.turnoSalvoLocations.salvoLocations.splice(salvoExistente, 1);
                document.getElementById(salvoL).classList.remove('salvo');
            }
            else
            {   //tiro nuevo
                //la idea es traerse el ultimo salvo del enemigo que sea anterior al turno en el que está el player, asi si player está en el turno 1 no va a encontrarlo y por ende la resta sería de 0. Y si lo encuentra se haría la resta con los hundidos del turno que corresponda
                var turn = gamesVue.getNextTurno(gp);
                                var sunks = new Set(gamesVue.gameView.salvos.filter(x => x.idGamePlayer != gp && x.turn <turn).flatMap( tsalvo => tsalvo.sinks).map(sinks => sinks.shipType));
                                var iSinkShips = 5 - sunks.size;

                if(gamesVue.turnoSalvoLocations.salvoLocations.length == iSinkShips ){
                    alert('You only have ' + iSinkShips + ' shots');
                    return;
                }

                gamesVue.turnoSalvoLocations.salvoLocations.push( letra + numero );
                document.getElementById(salvoL).classList.add('salvo');

            }
            /*for(var i =0; i < gamesVue.turnoSalvoLocations.length; i++ ){
                for(var j = 0;j < gamesVue.turnoSalvoLocations.salvoLocations.length; j++){
                    if()
                }
            }*/
        },

        isShipPlaced : function(type){
            var elenmtoencontrado = this.shipDefineds.find(element => element.shipType == type);
            return elenmtoencontrado != null;
        },

        placeShips : function(letra, numero){

            if(gamesVue.gameView.ships.length == 5)
                return;

            //eliminado de ships
            for(var j = 0; j < gamesVue.shipDefineds.length; j++){
                if(gamesVue.shipDefineds[j].shipLocations.find(x => x == letra+numero)){

                    //habilitar el input radio relacionado al ship
                    var shipSelected  = gamesVue.shipTypes.find(x => x.type == gamesVue.shipDefineds[j].shipType);
                    shipSelected.disabled = false;
                     gamesVue.shipSel = shipSelected;
                    // quitar clase ship de todas las posiciones que ese ship ocupaba
                    for(var i = 0; i < gamesVue.shipDefineds[j].shipLocations.length; i++){
                       document.getElementById(gamesVue.shipDefineds[j].shipLocations[i]).className = "";
                    }

                    //eliminar de shipdefineds el item del ship entero
                    gamesVue.shipDefineds.splice(j, 1);
                    return false;


                }
            }

            //Verifica cantidad
            if(gamesVue.shipDefineds.length == 5){
                alert('To many ships');
                return;
            }
            //Verifica orientacion
            if(gamesVue.orientationSel == null){
                alert('Select orientation');
                return;
            }

            var shipNameSel = gamesVue.shipSel.type;// $("input:radio[name=ship]:checked").attr('id');
            var shipLenSel = gamesVue.shipSel.length;//$("input:radio[name=ship]:checked").attr('value'); //o usar .val()

            //Verifica Ship
            if(shipNameSel == null){
                alert('Select ship');
                return false;
            }else{
                if(gamesVue.isShipPlaced(shipNameSel))
                {
                    alert('Ship' + shipNameSel + 'already placed');
                    return false;
                }
            }

            //Ya estariamos ok, todo validado. Vamos a calcular que positions le corresponden al ship
            var result = [ letra+numero ];
            if(gamesVue.orientationSel == "hor"){
                for(var i = 1; i< shipLenSel; i++){
                    if(numero+i > gamesVue.columns[gamesVue.columns.length-1]){
                        alert('Your Ship is out of range');
                        return false;
                    }else{
                        result.push(letra + (numero+i));
                    }
                }
            }else if(gamesVue.orientationSel == "ver"){
                for(var i = 1; i < shipLenSel; i++){
                    if(letra.charCodeAt(0)+i > 74){
                        alert('Your Ship is out of range')
                        return false;
                    }else{
                        result.push( (String.fromCharCode(letra.charCodeAt(0)+i)) + numero );
                    }
                }
            }

            for(var i = 0; i < result.length; i++){
                for(var j = 0; j < gamesVue.shipDefineds.length; j++){
                    if(gamesVue.shipDefineds[j].shipLocations.find(x => x == result[i])){
                        alert('Cell ' + result[i] + ' already ocupied');
                        return false;
                    }
                }
            }

            gamesVue.shipDefineds.push({ "shipType": shipNameSel, "shipLocations": result });
            gamesVue.shipTypes.find(x => x.type == shipNameSel).disabled = true;

            for(var i = 0; i < result.length; i++){
               document.getElementById(result[i]).classList.add(shipNameSel.replace(' ','_'));
            }
            return true;
        },

        addsalvos : function(){
            gamesVue.turnoSalvoLocations.turn = gamesVue.getNextTurno(gp);
            $.post({
                url: "/api/games/players/"+ gp +"/salvos",
                data: JSON.stringify(gamesVue.turnoSalvoLocations),

                dataType: "text",
                contentType: "application/json"
            })
            .done(function (response) {
                alert(JSON.parse(response).success);
                location.reload();
            })
            .fail(function (error) {
                alert("Failed to add salvos " + JSON.parse(error.responseText).error);
            })
        },

        addships : function(){
            var gpId = gp;

            $.post({
                url: "/api/games/players/"+ gpId +"/ships",
                data: JSON.stringify(gamesVue.shipDefineds),

                dataType: "text",
                contentType: "application/json"
            })
            .done(function (response) {
                alert(JSON.parse(response).success);
                location.reload()
            })
            .fail(function (error) {
                alert("Failed to add ships " + JSON.parse(error.responseText).error);
            })
        },

        cellSelect: function(cell, row){
            console.log(cell);
            console.log(row);
        },


        reloadurl : function (url){
                    location.href=url;
        },

        logout: function(){
                    $.post("/api/logout").done(function() {
                    location.href = "/web/games.html";
                    })
                },

        drawShips: function(){
            for(var i = 0; i < gamesVue.gameView.ships.length; i++){
                for(var j = 0 ; j < gamesVue.gameView.ships[i].shipLocation.length; j++){
                    document.getElementById(gamesVue.gameView.ships[i].shipLocation[j]).classList.add(gamesVue.gameView.ships[i].shipType.replace(' ','_'));
                }
            }
        },

        /*verifyHit: function(salvoLocation) {
            console.log('me tiraron en ' + salvoLocation);
            for(var i = 0; i < gamesVue.gameView.ships.length; i++){
                console.log('Vamo a verificar si le pego A: ' + gamesVue.gameView.ships[i].shipType)
                for(var l = 0; l < gamesVue.gameView.ships[i].shipLocation.length; l++){
                    console.log('Posicion ' + l + ' del ship = ' + gamesVue.gameView.ships[i].shipLocation[l]);
                    if(gamesVue.gameView.ships[i].shipLocation[l] == salvoLocation)
                    {
                        console.log('PUMMMMM LE PEGARON A ' + gamesVue.gameView.ships[i].shipType + ' EN ' + salvoLocation);
                        return true;
                    }
                }
            }

            console.log('NO ENCONTRO BARCO EN LA POSICION DEL TIRO en ' + salvoLocation);
            return false;
        },*/
        drawSalvoLocation: function(sTablePrefix, salvoLocation, className, turn)
        {
            var oCelda = document.getElementById(sTablePrefix + salvoLocation);
            oCelda.classList.add(className);
            oCelda.innerHTML = "<b>" + turn + "</b>";
        },

        drawSalvos: function(){
            for(var i = 0; i < gamesVue.gameView.salvos.length; i++){

                var oSalvo = gamesVue.gameView.salvos[i];
                var iTurno = oSalvo.turn;
                var sTablePrefix = "";

                if(oSalvo.idGamePlayer == gp)
                    sTablePrefix = "S";

                for(var j = 0 ; j < oSalvo.salvoLocation.length; j++){
                     gamesVue.drawSalvoLocation(sTablePrefix, oSalvo.salvoLocation[j], 'salvos', iTurno);
                }
                for(var j = 0 ; j < oSalvo.hits.length; j++){
                     gamesVue.drawSalvoLocation(sTablePrefix, oSalvo.hits[j], 'hit', iTurno);
                }
                for(var j = 0 ; j < oSalvo.sinks.length; j++){
                     gamesVue.drawSalvoLocation(sTablePrefix, oSalvo.sinks[j].location, 'sink', iTurno);
                     if(oSalvo.idGamePlayer == gp && !gamesVue.shipSinks.find(x => x == oSalvo.sinks[j].shipType))
                     {
                        gamesVue.shipSinks.push(oSalvo.sinks[j].shipType);
                     }
                }
            }
        },


        definePlayers: function(){
            //Otra forma de hacer esta funcion
            /*gamesVue.duenio = gamesVue.gameView.gamePlayers.find(x => x.id == gp).userName;
            gamesVue.verss = gamesVue.gameView.gamePlayers.find(x => x.id != gp).userName;*/

            //var ELDUIEBOI = gamesVue.gameView.gamePlayers.find(x => x.id == gp).userName;
            //document.getElementById("due").innerHTML = ELDUIEBOI;

            if(gamesVue.gameView.gamePlayers[0].gpId == gp)
            {
                gamesVue.players.viewer = gamesVue.gameView.gamePlayers[0];
                if(gamesVue.gameView.gamePlayers.length > 1){
                    gamesVue.players.enemy = gamesVue.gameView.gamePlayers[1];
                }
            }
            else
            {
                gamesVue.players.enemy = gamesVue.gameView.gamePlayers[0];
                gamesVue.players.viewer = gamesVue.gameView.gamePlayers[1];
            }
        },
        wait: function(){
            if(gamesVue.gameView.status == "WAITING_OPONENT" || gamesVue.gameView.status == "WAIT_OPONENT_PLACING_SHIPS" || gamesVue.gameView.status == "WAIT")
                setTimeout( () => { gamesVue.getData(); }, 2000); //alternativa setTimeout( gamesVue.getData, 2000);
        },

        getData: function(){
            fetch("/api/game_view/" + gp)
            .then(function(response){
                if(response.ok){
                    return response.json();
                }
                throw new error (response.status);
            })
            .then(function(json){
                gamesVue.gameView = json;
                //document.getElementById('divJson').innerText = JSON.stringify(json, null, 4);
                //$('#divJson').html(JSON.stringify(json, null, 4));
                console.log(json);
                gamesVue.drawShips();
                gamesVue.definePlayers();
                gamesVue.drawSalvos();
                gamesVue.getStatus();
                gamesVue.wait();
            })
        }
    },
});


const urlParams = new URLSearchParams(window.location.search);
const gp = urlParams.get('gp');

gamesVue.getData();




