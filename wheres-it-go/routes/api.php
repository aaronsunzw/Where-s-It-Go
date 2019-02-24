<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/
//
//Route::middleware('auth:api')->get('/user', function (Request $request) {
//    return $request->user();
//});

Auth::routes();

Route::any('/auth', 'UserController@postLogin');

Route::group(array('prefix' => ''), function() {
    //AdvancedRoute::controller('/user', 'UserController');
    Route::post('user/register', 'UserController@postRegister');
    AdvancedRoute::controller('category', 'CategoryController');
    AdvancedRoute::controller('record', 'RecordController');
});
