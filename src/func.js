//функция извлекает из ответа название города или страны и их координаты и сохраняет в сессионные переменные
function getLocation (answer){
    if (answer.City) {
        $jsapi.context().session.place = {name: answer._City.name, namesc: "", type: "city"};
        $jsapi.context().session.coordinates = {lat: answer._City.lat, lon: answer._City.lon};
    }
    else {
        $jsapi.context().session.place = {name: answer._Country.name, namesc: answer._Country.namesc, type: ""};
        $jsapi.context().session.coordinates = {lat: answer._Country.lat, lon: answer._Country.lon};
    }
}

//функция запрашивает прогноз погоды и возвращает параметры прогноза либо ответ о неудаче
function getWeather(lat, lon, day) {
	var response = $http.get("https://api.weatherbit.io/v2.0/forecast/daily?lat=${lat}&lon=${lon}&lang=${lang}&key=${key}&days=16", {
            timeout: 10000,
            query:{
                // новый ключ
                // key: "c31a720db0464fbb8ff87abd57a16a0b",
                // старый ключ
                key: "0ee3493daca046aea3dbe81b076f4083",
                lang: "ru",
                lat: lat,
                lon: lon,
                days: toPrettyString(day)
            }
        });
	if (!response.isOk || !response.data) return false

	var weather = {};
	weather.temp = response.data.data[day].temp;
	weather.wind = response.data.data[day].wind_spd;
	weather.gust = response.data.data[day].wind_gust_spd;
	weather.descript = response.data.data[day].weather.description;

	return weather;
}

//функция запрашивает исторические данны о погоде и возвращает параметры прогноза либо ответ о неудаче
function getHistoricalWeather(lat, lon, start_date, end_date) {
	var response = $http.get("https://api.weatherbit.io/v2.0/history/daily?lat=${lat}&lon=${lon}&start_date=${start_date}&end_date=${end_date}&key=${key}", {
            timeout: 10000,
            query:{
                key: "0ee3493daca046aea3dbe81b076f4083",
                lat: lat,
                lon: lon,
                start_date: toPrettyString(start_date),
                end_date:  toPrettyString(end_date)
            }
        });
	if (!response.isOk || !response.data) return false
        
	var weather = {};
	weather.temp = response.data.data[0].temp;
	weather.wind = response.data.data[0].wind_spd;
	weather.gust = response.data.data[0].wind_gust_spd;

	return weather;
}

//функция отнимает один из числа в формате строки
function minus(num) {
	var number = parseInt(num);
	return (number - 1);
}	

//функция добавляет один к числу в формате строки
function plus(num) {
	var number = parseInt(num);
	return (number + 1);
}	

//функция формирует заявку из сессионных переменных и отправляет её на почту
function email () {
    var subject = "Заявка от клиента " + $jsapi.context().client.name;
    var form = "";
    form += " Комментарий для Менеджера: " + $jsapi.context().client.note + "<br>";
            
    var answer = {}    
    answer = $mail.send({
        from: "jaicp_mfti@mail.ru",
        to: ["jaicp_mfti@mail.ru"],
        subject: subject,
        content: form,
        smtpHost: "smtp.mail.ru",
        smtpPort: "465",
        user: "jaicp_mfti@mail.ru",
        password: "eaxhXE3gXmc1eKnyYGYg"
    });
    return (answer);
}

