Vue.filter('states', function (gameState) {
    if (gameState == "PLAYER_PLACES_SALVOES") {
        return "Choose 5 cells then press FIRE🔥!"
    } else if (gameState == "PLAYER_PLACES_SHIPS") {
        return "Place your fleet, when you're ready... press the button!"
    } else if (gameState == "WAITING_OPPONENT") {
        return "Wait for an opponent to start the game!"
    } else if (gameState == "OPPONENT_PLACES_SHIPS") {
        return "Your opponent is placing his fleet, please wait...⏲"
    } else if (gameState == "OPPONENT_PLACES_SALVOES") {
        return "Your opponent is placing his shots... please wait for your turn⏲"
    } else if (gameState == "PLAYER_WINS") {
        return "You WIN 🏁!"
    } else if (gameState == "PLAYER_LOSE") {
        return "You LOSE☠"
    } else if (gameState == "PLAYERS_TIE") {
        return "TIE🏳"
    }
})

var app = new Vue({
    el: '#app',
    data: {
        gamePlayerId: null,
        ships: [],
        grid: null,
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        columns: ["🔥", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        gameView: {},
        playerOne: {},
        playerTwo: {},
        salvoesPlayerTwo: [],
        shipsLocation: [],
        salvoesTosend: [],
        gameState: "",





    },

    methods: {
        createGrid: function (isStatic) {
            const options = {
                //grilla de 10 x 10
                column: 10,
                row: 10,
                //separacion entre elementos (les llaman widgets)
                verticalMargin: 0,
                //altura de las celdas
                disableOneColumnMode: true,
                //altura de las filas/celdas
                cellHeight: 40,
                //necesario
                float: true,
                //desabilitando el resize de los widgets
                disableResize: true,
                //false permite mover los widgets, true impide
                staticGrid: isStatic,

            }
            //iniciando la grilla en modo libe statidGridFalse
            app.grid = GridStack.init(options, '#grid');


        },
        paintShips: function () {
            this.gameView.ships.forEach(ship => {
                var vertical = false;
                var ubicacion = ship.locations[0];
                var ubicacion2 = ship.locations[1];
                var y = conv2(ubicacion.charAt(0));
                var x = parseInt(ubicacion.slice(1)) - 1;
                if (ubicacion[0] != ubicacion2[0]) {
                    vertical = true;
                }

                if (ship.type == 'destroyer') {
                    if (vertical) {
                        app.grid.addWidget('<div><div id="destroyer" class="grid-stack-item-content destroyerVertical"></div><div/>',
                            x, y, 1, 3);
                    } else {
                        app.grid.addWidget('<div><div id="destroyer" class="grid-stack-item-content destroyerHorizontal"></div><div/>',
                            x, y, 3, 1);

                    }
                } else if (ship.type == 'patrol') {
                    if (vertical) {
                        app.grid.addWidget('<div><div id="patrol" class="grid-stack-item-content patrolVertical"></div><div/>',
                            x, y, 1, 2);
                    } else {
                        app.grid.addWidget('<div><div id="patrol" class="grid-stack-item-content patrolHorizontal"></div><div/>',
                            x, y, 2, 1);
                    }

                } else if (ship.type == 'battleship') {
                    if (vertical) {
                        app.grid.addWidget('<div><div id="battleship" class="grid-stack-item-content battleshipVertical"></div><div/>',
                            x, y, 1, 5);
                    } else {
                        app.grid.addWidget('<div><div id="battleship" class="grid-stack-item-content battleshipHorizontal"></div><div/>',
                            x, y, 5, 1);
                    }

                } else if (ship.type == 'carrier') {
                    if (vertical) {
                        app.grid.addWidget('<div><div id="carrier" class="grid-stack-item-content carrierVertical"></div><div/>',
                            x, y, 1, 4);
                    } else {
                        app.grid.addWidget('<div><div id="carrier" class="grid-stack-item-content carrierHorizontal"></div><div/>',
                            x, y, 4, 1);

                    }
                } else if (ship.type == 'submarine') {
                    if (vertical) {
                        app.grid.addWidget('<div><div id="submarine" class="grid-stack-item-content submarineVertical"></div><div/>',
                            x, y, 1, 3);
                    } else {
                        app.grid.addWidget('<div><div id="submarine" class="grid-stack-item-content submarineHorizontal"></div><div/>',
                            x, y, 3, 1);
                    }
                }
            })

        },
        createShips: function () {
            //todas las funciones se encuentran en la documentación
            //https://github.com/gridstack/gridstack.js/tree/develop/doc

            //agregando elementos (widget) desde el javascript
            //elemento,x,y,width,height
            app.grid.addWidget('<div><div id="submarine" class="grid-stack-item-content submarineHorizontal"></div><div/>',
                0, 1, 3, 1);

            app.grid.addWidget('<div><div id="carrier" class="grid-stack-item-content carrierHorizontal"></div><div/>',
                0, 3, 4, 1);

            app.grid.addWidget('<div><div id="patrol" class="grid-stack-item-content patrolHorizontal"></div><div/>',
                0, 0, 2, 1);

            app.grid.addWidget('<div><div id="destroyer" class="grid-stack-item-content destroyerHorizontal"></div><div/>',
                0, 2, 3, 1);

            app.grid.addWidget('<div><div id="battleship" class="grid-stack-item-content battleshipHorizontal"></div><div/>',
                0, 4, 5, 1);


            //rotacion de las naves
            //obteniendo los ships agregados en la grilla
            app.ships = document.querySelectorAll("#submarine,#carrier,#patrol,#destroyer,#battleship");
            app.ships.forEach(ship => {
                //asignando el evento de click a cada nave
                ship.parentElement.onclick = function (event) {
                    //obteniendo el ship (widget) al que se le hace click
                    let itemContent = event.target;
                    //obteniendo valores del widget
                    let itemX = parseInt(itemContent.parentElement.dataset.gsX);
                    let itemY = parseInt(itemContent.parentElement.dataset.gsY);
                    let itemWidth = parseInt(itemContent.parentElement.dataset.gsWidth);
                    let itemHeight = parseInt(itemContent.parentElement.dataset.gsHeight);

                    //si esta horizontal se rota a vertical sino a horizontal
                    if (itemContent.classList.contains(itemContent.id + 'Horizontal')) {
                        //veiricando que existe espacio disponible para la rotacion
                        if (app.grid.isAreaEmpty(itemX, itemY + 1, itemHeight, itemWidth - 1) && (itemY + (itemWidth - 1) <= 9)) {
                            //la rotacion del widget es simplemente intercambiar el alto y ancho del widget, ademas se cambia la clase
                            app.grid.resize(itemContent.parentElement, itemHeight, itemWidth);
                            itemContent.classList.remove(itemContent.id + 'Horizontal');
                            itemContent.classList.add(itemContent.id + 'Vertical');
                        } else {
                            alert("Espacio no disponible");
                        }
                    } else {
                        if (app.grid.isAreaEmpty(itemX + 1, itemY, itemHeight - 1, itemWidth) && (itemX + (itemHeight - 1) <= 9)) {
                            app.grid.resize(itemContent.parentElement, itemHeight, itemWidth);
                            itemContent.classList.remove(itemContent.id + 'Vertical');
                            itemContent.classList.add(itemContent.id + 'Horizontal');
                        } else {
                            alert("Espacio no disponible");
                        }
                    }
                }
            })

        },
        postShips: function () {
            app.saveShips();
            $.post({
                    url: "/api/games/players/" + app.gamePlayerId + "/ships",
                    data: JSON.stringify(app.shipsLocation),
                    dataType: "text",
                    contentType: "application/json"
                })
                .done(function () {
                    alert("Wait for the other player");
                    location.reload(true);
                })
                .fail(function (httpError) {
                    alert("Failed to add ships:" + httpError);
                })
        },
        saveShips: function () {
            arrayBoats = Array.from(app.ships);
            var totalShips = [];
            app.shipsLocation = totalShips;
            for (let i = 0; i < arrayBoats.length; i++) {
                var ship = {
                    type: "",
                    locations: [],
                };
                ship.type = arrayBoats[i].id;
                if (arrayBoats[i].parentElement.dataset.gsHeight == "1") {
                    for (let j = 0; j < parseInt(arrayBoats[i].parentElement.dataset.gsWidth); j++) {
                        ship.locations.push(conv(arrayBoats[i].parentElement.dataset.gsY) + (parseInt(arrayBoats[i].parentElement.dataset.gsX) + j + 1));
                    }
                } else {
                    for (let j = 0; j < parseInt(arrayBoats[i].parentElement.dataset.gsHeight); j++) {
                        ship.locations.push(conv((parseInt(arrayBoats[i].parentElement.dataset.gsY) + j) + "") + (parseInt(arrayBoats[i].parentElement.dataset.gsX) + 1));
                    }
                }
                totalShips.push(ship);
            }
            return totalShips;
        },
        postSalvoes: function () {
            $.post({
                    url: "/api/games/players/" + app.gamePlayerId + "/salvoes",
                    data: JSON.stringify(app.salvoesTosend),
                    dataType: "text",
                    contentType: "application/json",
                })
                .done(function () {
                    alert("Sending your shots...!");
                    location.reload(true);
                })
                .fail(function () {
                    alert("Please choose 5 shots!");
                })
        },
        selectSalvoes: function (cell) {
            if (app.gameState == "PLAYER_PLACES_SALVOES") {
                if (app.salvoesTosend.includes(cell)) {
                    document.getElementById(cell).classList.remove("salvo");
                    app.salvoesTosend = app.salvoesTosend.filter(e => e != cell);
                } else if (document.getElementById(cell).classList.length == 0) {
                    if (app.salvoesTosend.length < 5) {
                        document.getElementById(cell).classList.add("salvo");
                        app.salvoesTosend.push(cell)
                    } else {
                        alert("You run out of salvoes")
                    }
                }
            }

        },
        paintMiss: function () {
            this.gameView.salvoes.forEach(x => {
                x.locations.forEach(location => {
                    if (x.player == app.playerOne.id) {
                        document.getElementById(location).classList.add("water");
                        document.getElementById(location).innerHTML = x.turn;
                    }
                })
            })
        },
        paintHits: function () {
            this.gameView.hits.forEach(x => {
                x.hits.forEach(hits => {
                    if (x.player == app.playerOne.id) {
                        document.getElementById(hits).classList.add("hit");
                        document.getElementById(hits).classList.remove("water");
                        document.getElementById(hits).innerHTML = x.turn;
                    }
                })
            })
        },
        paintSunks: function () {
            this.gameView.sunks.forEach(x => {
                x.sunks.forEach(sunks => {
                    sunks.locations.forEach(y => {
                        if (x.player == app.playerOne.id) {
                            document.getElementById(y).classList.add("sunk");
                            document.getElementById(y).classList.remove("hit");
                            document.getElementById(y).classList.remove("water");
                        }
                    })
                })
            })
        },
        sortSalvoes: function () {
            this.gameView.hits.sort(function (a, b) {
                if (a.turn > b.turn) {
                    return -1;
                }
                if (a.turn < b.turn) {
                    return 1;
                }
                return 0;
            })
        },
        sortOppSalvoes: function () {
            this.gameView.oppHits.sort(function (a, b) {
                if (a.turn > b.turn) {
                    return -1;
                }
                if (a.turn < b.turn) {
                    return 1;
                }
                return 0;
            })
        },
    }
})


app.gamePlayerId = paramObj(location.search).gp;

fetch("/api/game_view/" + app.gamePlayerId)
    .then((response) => {
        if (response.ok) {
            return response.json();
        } else {
            return Promise.reject(response.json())
        }

    })
    .then(function (myJson) {
        app.gameView = myJson;
        app.playerOne = app.gameView.gamePlayers.filter(x => x.id == app.gamePlayerId)[0].player;
        var players = app.gameView.gamePlayers.filter(x => x.id != app.gamePlayerId);
        if (players.length > 0)
            app.playerTwo = players[0].player;
        app.salvoesPlayerTwo = app.gameView.salvoes.filter(x => x.player != app.gamePlayerId);
        app.gameState = myJson.gameState;

        if (myJson.ships.length > 0) {
            app.createGrid(true);
        } else {
            app.createGrid(false);
        }


        if (app.gameView.ships.length > 0) {
            app.paintShips();
        } else {
            app.createShips();

        }

        if (app.gameView.salvoes.length > 0) {
            app.paintMiss();
            app.paintHits();
            app.paintSunks();
            app.sortSalvoes();
            app.sortOppSalvoes();
        }

        start();

    });



function paramObj(search) {
    var obj = {};
    var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

    search.replace(reg, function (match, param, val) {
        obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });

    return obj;
}

function conv(letter) {
    switch (letter) {
        case "0":
            return "A";
            break;
        case "1":
            return "B";
            break;
        case "2":
            return "C";
            break;
        case "3":
            return "D";
            break;
        case "4":
            return "E";
            break;
        case "5":
            return "F";
            break;
        case "6":
            return "G";
            break;
        case "7":
            return "H";
            break;
        case "8":
            return "I";
            break;
        case "9":
            return "J";
            break;
        case "10":
            return "K";
            break;
    }
}

function conv2(letter) {
    switch (letter) {
        case "A":
            return 0;
            break;
        case "B":
            return 1;
            break;
        case "C":
            return 2;
            break;
        case "D":
            return 3;
            break;
        case "E":
            return 4;
            break;
        case "F":
            return 5;
            break;
        case "G":
            return 6;
            break;
        case "H":
            return 7;
            break;
        case "I":
            return 8;
            break;
        case "J":
            return 9;
            break;
        case "K":
            return 10;
            break;
    }
}
var timerId;
/*
function start() {
    if (app.gameState == "OPPONENT_PLACES_SALVOES" || app.gameState == "WAITING_OPPONENT" || app.gameState == "OPPONENT_PLACES_SHIPS") {
        timerId = setTimeout(function () {
            location.reload();
        }, 10000);
    }
}
*/