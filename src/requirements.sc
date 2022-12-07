# модуль для работы со слотфилингом
require: slotfilling/slotFilling.sc
  module = sys.zb-common

# библиотека функций Moment.js - для работы с текущей датой
require: dateTime/moment.min.js
    module = sys.zb-common 

# библиотека пользовательских функций
require: func.js

# файл сценария с заполнением заявки на тур
require: trip.sc

# файл сценария с обработкой запросов на прогноз погоды
require: weather.sc

# файл сценария с паттернами локальных переменных
require: localPatterns.sc  

# справочник городов
require: dicts/cities.csv
    name = cities
    var = cities

# справочник стран
require: dicts/countries.csv
    name = countries
    var = countries

# справочник имен
require: dicts/names.csv
    name = names
    var = names
    
# справочник названий месяцев
require: dicts/months.yaml
    name = months
    var = months
