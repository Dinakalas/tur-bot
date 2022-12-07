theme: /Trip
    
    state: Begin
    #если заявку начинали оформлять то идем туда где остановились, иначе идем проверять/спрашивать имя
    # вводим счётчик попыток ввести имя
        script: $session.nameTryCount = 0;
        if: $session.tripStep
            script: $reactions.transition ( {value:$session.tripStep, deferred: false} );
        else: 
            a: Для оформления заявки я задам вам несколько вопросов. 
            if: $client.name
                go!: /Trip/Step2
            else: 
                a: Обязательными будут только Ваше имя и телефон.
                go!: /Trip/AskName
    #если имени нет - запрашиваем его
    state: AskName || modal = true
        a: Пожалуйста, введите Ваше имя
    #имя совпало с переменной из списка - сохраняем имя, идем на Шаг2 заявки
        state: GetName
            q: * $Name *
            script: $client.name = $parseTree._Name.name;
            a: {{$client.name}}, приятно познакомиться!    
            go!: /Trip/Step2
    #выход из модального стейта при желании выйти из диалога
        state: GoExit
            q: $cancel 
            go!: /Exit         
    #не хочу знакомиться - извиняемся и идем на выход
        state: NoName
            q: $refuse
            q: ( * (не надо)/(не хочу)/(не буду) * )
            if: $session.nameTryCount == 0
                a: Без имени я не могу принять заявку на тур. Давайте попробуем ещё раз.
                script: $session.nameTryCount +=1
                go!: /Trip/AskName
            else: 
                a: Вижу, Вы не хотите сообщать имя, а мне без него не принять заявку. Введёте имя или отменим заявку?
                buttons:
                    "введу имя"
                    "отменяю заявку"
            state: EnterName
                    q: * введу *
                    q: $yes
                    go!: /Trip/AskName
            state: CancelTrip
                    q: * отменяю *
                    q: $no
                    q: $refuse
                    go!:/Exit
    #выход из модального стейта при запросе погоды
        state: GoWeather
            intent: /Погода
            go!: /Weather/Begin
    #другое непонятное слово - уточняем имя это или нет
        state: GetStrangeName
            event: noMatch
            script: $session.tempName = $request.query;
            a: {{$session.tempName}}! Какое необычное имя. Вы не ошиблись? Я могу вас так называть?
            buttons:
                "Да"
                "Нет"
    #если имя - сохраняем его и идем на Шаг2 заявки
            state: Yes
                q: $yes
                script: $client.name = $session.tempName;
                a: {{$client.name}}, приятно познакомиться!
                go!: /Trip/Step2
    #если не имя - пробуем с начала
            state: No
                q: $no
                q: ((* ошиб* *)/(* не може* *))
                a: Попробуем еще раз.
                go!: /Trip/AskName
            state: GetNameHere
                q: * $Name *
                go!: /Trip/AskName/GetName
            state: NoMatchHere
                event: noMatch
                a: Подтвердить имя "{{$session.tempName}}" - введите "Да"
                   Изменить имя - введите "Нет"
                buttons:
                    "Да"
                    "Нет"              
                    
    #если телефон есть, идем дальше, иначе - идем его спрашивать
    state: Step2
        script: $session.tripStep = "/Trip/Step2"
        if: $client.phone
            go!: /Trip/Step3
        else:
            script: $session.phoneTryCount = 0;
            go!: /Trip/AskPhone
    #если телефона нет - запрашиваем его
    state: AskPhone || modal=true
        a: Пожалуйста, введите номер Вашего телефона в формате 8-123-456-78-90.
    #имя совпало с переменной из списка - сохраняем имя, идем на Шаг2 заявки
        state: GetPhone
            q: * $phone *
            script: $client.phone = $parseTree._phone
            a: {{$client.phone}} внесла в заявку
            if: $session.note1
                go!: /Tour/Confirm 
            go!: /Trip/Step3
    #выход из модального стейта при желании выйти из диалога
        state: GoExit
            q: $cancel 
            go!: /Exit        
            
    #не хочу знакомиться - извиняемся и идем на выход
        state: NoPhone
            q: $refuse
            q: ( * (не надо)/(не хочу)/(не буду) * )
            if: $session.phoneTryCount == 0
                a: Без номера телефона я не могу принять заявку на тур. Давайте попробуем ещё раз.
                script: $session.phoneTryCount +=1
                go!: /Trip/AskPhone
            else: не 
                a: Вижу, Вы не хотите сообщать номер, а мне без него не принять заявку. Введёте номер или отменим заявку?
                buttons:
                    "введу номер"
                    "отменяю заявку"
            state: EnterPhone
                    q: * введу *
                    q: $yes
                    go!: /Trip/AskPhone
            state: CancelTrip
                    q: * отменяю *
                    q: $no
                    q: $refuse
                    go!:/Exit
    #иное - ругаемся и идем на начало
        state: NoMatch
            event: noMatch
            a: Непохоже, что это номер телефона в нужном формате. Попробуем еще раз? Для выхода напишите "стоп"
            go!: /Trip/AskPhone
            
    #запрашиваем дату   
    state: Step3
        script: $session.tripStep = "/Trip/Step3"        
        if: $session.note
            script:
                delete $session.note
            go!: /Tour/Confirm

        a: Назовите дату начала поездки. Можно примерно
        buttons:
            "Еще не знаю"
    #введена дата - сохраняем ее
        state: Date
            q: * @duckling.date *
            script: $session.date = $parseTree.value;
            go!: /Trip/Step4
    #введено что-то иное - сохраняем это в другой переменной
        state: NoDate
            event: noMatch
            script: $session.noDate = $request.query;
            go!: /Trip/Step4
    
    #запрашиваем длительность поездки
    state: Step4 
        script: $session.tripStep = "/Trip/Step4"
        a: Выберите длительность поездки
        buttons:
            "до 7 дней"
            "7-13 дней"
            "14-20 дней"
            "21-29 дней"
            "Свыше месяца"
            "Еще не знаю"
    #подойдет любой ответ - записали его и идем на Шаг5 заявки
        state: NoMatch
            event: noMatch
            script: $session.duration = $request.query;
            go!: /Trip/Step5

    #запрашиваем количество участников поездки
    state: Step5 
        script: $session.tripStep = "/Trip/Step5"
        a: Сколько всего человек будет в поездке включая детей?
        buttons:
            "Еще не знаю"
        
    #если введено число - записали его
        state: Number
            q: * @duckling.number * || fromState = "/Trip/Step5", onlyThisState = true
            script: $session.people = $request.query;
            # если число больше 1 - пошли спросить про детей
            if: ($parseTree.value > 1)
                go!: /Trip/Step5/Children
            else: 
                go!: /Trip/Step6
    #спросили про детей 
        state: Children
            a: Сколько из них детей младше 14 лет?
    #подойдет любой ответ - записали его и идем на Шаг6 заявки
            state: NoMatch
                event: noMatch
                script: $session.children = $request.query;
                go!: /Trip/Step6
    #если любой другой ответ из корня Step5 - записали его как есть и идем на Шаг6 заявки
        state: Answer
            event: noMatch || fromState = "/Trip/Step5", onlyThisState = true
            script: $session.people = $request.query;
            go!: /Trip/Step6
            
    #запрашиваем бюджет поездки
    state: Step6 
        script: $session.tripStep = "/Trip/Step6"
        a: Какой примерно бюджет поездки из расчета на одного взрослого?
        buttons:
            "до $300"        
            "$300-$700"        
            "$700-$1500"        
            "$1500-$3000"
            "свыше $3000"
            "Еще не знаю"
    #подойдет любой ответ - записали его и идем на Шаг7 заявки
        state: NoMatch
            event: noMatch
            script: $session.budget = $request.query;
            go!: /Trip/Step7

    #запрашиваем предпочтение отеля
    state: Step7 
        script: $session.tripStep = "/Trip/Step7"
        a: Какой хотите минимальный уровень звездности отеля?
        buttons:
            "не важно"        
            "3*"        
            "4*"        
            "5*"
            "Еще не знаю"
    #подойдет любой ответ - записали его и идем на Шаг8 заявки
        state: NoMatch
            event: noMatch
            script: $session.stars = $request.query;
            go!: /Trip/Step8

    #запрашиваем о пожеланиях, которые не вошли в заявку
    state: Step8 || modal = true
        script: $session.tripStep = "/Trip/Step8"
        a: Что-то еще передать менеджеру? Может, какие-то пожелания?
        buttons:
            "пожеланий нет" 
    #подойдет любой ответ - записали его и идем формировать почту
        state: NoMatch || modal = true
            event: noMatch
            script:
                $session.tour_note = $request.query
                $session.note1 = $session.tour_note;
                delete $session.tour_note;
            go!: /Tour/Confirm    
            
#============================================= Проверка итоговой заявки =====================================================#    
theme: /Tour
    state: Confirm
        script:
    
            var subject = "Заявка от клиента " + $jsapi.context().client.name;
            var form = "";
            form += " Имя: " + $jsapi.context().client.name + "<br>";
            form += " Телефон: " + $jsapi.context().client.phone + "<br>";
            if ($jsapi.context().session.place) form += " Пункт назначения: " + $jsapi.context().session.place.name + "<br>";
                else form += " Пункт назначения: не определен"  + "<br>";
            if ($jsapi.context().session.date) form += " Дата начала поездки: " + $jsapi.context().session.date.day + "." + $jsapi.context().session.date.month + "." + $jsapi.context().session.date.year + "<br>";
                else form += " Дата начала поездки: " + $jsapi.context().session.noDate + "<br>";
            form += " Длительность поездки: " + $jsapi.context().session.duration + "<br>";
            form += " Количество людей: " + $jsapi.context().session.people + "<br>";
            if ($jsapi.context().session.children) form += " Количество детей: " + $jsapi.context().session.children + "<br>";
                else form += " Количество детей: не определено"  + "<br>";
            form += " Бюджет на одного взрослого: " + $jsapi.context().session.budget + "<br>";
            form += " Минимальная звездность отеля: " + $jsapi.context().session.stars + "<br>";
            form += " Комментарий для Менеджера: " + $session.note1 + "<br>";
            $session.note = form
       
        a: Проверьте Вашу заявку: 
        a:{{$session.note}}
        a: Все верно?
        
        buttons:
            "Верно"
            "Изменить имя"
            "Изменить телефон"
            "Отменить заявку"
        
        state: AcceptedNote
            q: (да/Верно)
            go!: /SendMail/Mail

        state: ChangeName
            q: * (имя/Изменить имя) *
            script:
                delete $client.name
            go!: /Trip/AskName

            
        state: ChangePhone
            q: * (телефон/Изменить телефон) *
            script:
                delete $client.phone
            go!: /Trip/AskPhone
            
        state: CanselTour
            q: * (отменить/Отменить заявку) *
            script:
                $session = {}
            go!: /Menu/Choose
 
#============================================= Отправка формы на почту менеджеру =============================================#            
        
theme: /SendMail
    state: Mail
        a: Заявка сформирована, отправляю в компанию Just Tour...
    #формируем и отправляем заявку
        script: $session.result = email ()
        #если результат ОК - сообщаем об этом и идем на четкий выход
        if: ($session.result.status == "OK")
            a: Заявка отправлена. Менеджер Just Tour свяжется с вами в ближайшее время
            go!: /End

    #если результат не ОК - тоже сообщаем об этом и идем на мягкий выход
        else:
            a: Упс. Ваша заявка не отправилась. Для подбора тура обратитесь в JustTour по телефону 8-812-000-000.
            go!: /End