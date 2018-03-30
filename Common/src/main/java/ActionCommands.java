public enum ActionCommands {
    NEW_USER,   //регистрация нового пользователя
    AUTH_USER,  //авторизация пользователя
    ADD_CLIENT, //добавление нового клиента для существующего пользователя
    GET_LIST,   //запрос списка файлов с сервера
    GET_FILE,   //запрос на скачивание файла
    DELETE_FILE //запрос на удаление файла
}