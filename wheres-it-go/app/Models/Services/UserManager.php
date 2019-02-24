<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace App\Models\Services;
use Illuminate\Database\Eloquent\Model;
use Carbon\Carbon;
use App\Models\Entities\User;
use Hash;
use App\Models\Services\AuthManager;

/**
 * Description of UserController
 *
 * @author aaron
 */
class UserManager {
    //put your code here
    private $response;

    public function __construct(AuthManager $authManager) {
        $this->AuthManager = $authManager;
    }

    public function register($data) {
        //status =0, failed generic error
        //status =1  register successfully
        //status =2  eisiting loginid
        //status =3  existing email
        $this->response["auth"] = 0;
        try {
            $user = new User;
            $user->name = $data['name'];
            $user->username = $data['username'];
            $user->password = bcrypt($data["password"]);
            $user->email = $data["email"];
            $user->save();
            $this->response["user"] = $user;
            $this->response["response"]['message'] = "Successfully registered";
            $this->response["response"]["status"] = "1";
        } catch (\Exception $e) {
            $this->response["response"] = array("message" => "register error", "status" => '0');
            $this->response["response"]["message"] = $e->getMessage();
        }

        return array("json" => $this->response);
    }

    public function login($username, $password) {
        //default ==failed
        //status =0, failed generic error
        //status =1 success
        //status =2, error authentication, password wrong
        try {
            $this->response = $this->AuthManager->auth($username, $password);
            $this->response["response"]["status"] = "1";
             return array("json" => $this->response, "user" => $this->response["user"]);
        } catch (\Exception $e) {
           
            $this->response["response"]["status"] = "0";
            $this->response["response"]["message"] = $e->getMessage();
             return array("json" => $this->response, "user" => NULL);
            }
       
    }
}
