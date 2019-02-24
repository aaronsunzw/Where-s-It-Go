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
/**
 * Description of AuthManager
 *
 * @author aaron
 */


class AuthManager {
    //put your code here
    private $response;

    public function auth($username, $password) {
        $this->response["auth"] = 0;
        
        try {
            $user = User::where('username', $username)->firstOrFail();

            if (Hash::check($password, $user->password)) {
                $this->response["auth"] = 1;
                //password match
                $this->response["response"]["message"] = "Login successful";
                //  $this->response["userDetails"] = $user->toArray();
                $this->response["user"] = $user;
            } else {
                $this->response["auth"] = 0;
                $this->response["response"]["status"] = "2";
                $this->response["response"]["message"] = "Authentication Error";
            }
        } catch (\Exception $e) {
            $this->response["response"] = array("message" => "Login error", "status" => '0');
            $this->response["response"]["message"] = $e->getMessage();
        }
        
        return $this->response;
    }
}
