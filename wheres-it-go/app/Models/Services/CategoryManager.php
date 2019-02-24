<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace App\Models\Services;
use Illuminate\Database\Eloquent\Model;
use Carbon\Carbon;
use App\Models\Entities\Category;
use Hash;
use App\Models\Services\AuthManager;

/**
 * Description of CategoryManager
 *
 * @author aaron
 */
class CategoryManager {
    //put your code here
    public function __construct(AuthManager $authManager) {
        $this->AuthManager = $authManager;
    }
    
    public function retrieveCategories($data) {
        //status =0, failed generic error
        //status =1  register successfully
        //status =2  eisiting loginid
        //status =3  existing email
        try {
            $this->response = $this->AuthManager->auth($data["username"], $data["password"]);
            if ($this->response["auth"] == "1") {
                $allCategories = Category::where('user_id', '=', $data['user_id'])->get();
                
                if(count($allCategories) != 0)
                {
                    $categoryArray = array();
                    
                    for ($i = 0; $i < count($allCategories); $i++) {
                        $category = $allCategories[$i];
                        $categoryArray[$i] = $allCategories[$i];
                    }
                    
                    $this->response['response']['category'] = $categoryArray;
                    $this->response["response"]['message'] = "Successfully retrieved ";
                    
                }else
                    $this->response["response"]['message'] = "No Categories";
                
                $this->response["response"]["status"] = "1";
            } else {
                $this->response['response']['status'] = '0';
                $this->response['message'] = "Please Login";
            }
        } catch (\Exception $e) {
            $this->response["response"] = array("message" => "Retrieve error", "status" => '0');
            $this->response["response"]["message"] = $e->getMessage();
        }
        //$this->response['response']['created_by'] = Trail::where('created_by', '=', $created_by)->get();
        return array("json" => $this->response);
    }
    
    
    public function createCategory($data) {
        try {
            $this->response = $this->AuthManager->auth($data["username"], $data['password']);
            if ($this->response['auth'] == '1') {
                $category = new Category;
                $category->user_id = $data['user_id'];
                $category->category_title = $data['category_title'];
                $category->category_description = $data['category_description'];
                $category->save();
                $this->response['response']['status'] = '1';
                $this->response['response']['message'] = 'Category successfully created';
            } else {
                $this->response['response']['status'] = '0';
                $this->response['response']['message'] = 'You are not authorized';
            }
        } catch (\Exception $e) {
            $this->response['response']['status'] = '0';
            $this->response['message'] = $e->getMessage();
            return array("json" => $this->response);
        }
        return array("json" => $this->response);
    }
    
    public function deleteCategory($data){
        try {
            $this->response = $this->AuthManager->auth($data["username"], $data["password"]);
            if ($this->response["auth"] == "1") {
                Category::where('user_id', '=', $data['user_id'])
                        ->where('category_id', '=', $data['category_id'])
                        ->delete();
                
                $this->response["response"]['message'] = "Category Successfully deleted.";
                $this->response["response"]["status"] = "1";
            } else {
                $this->response['response']['status'] = '0';
                $this->response['message'] = "Please Login";
            }
        } catch (\Exception $e) {
            $this->response["response"] = array("message" => "Retrieve error", "status" => '0');
            $this->response["response"]["message"] = $e->getMessage();
        }
        
        return array("json" => $this->response);
    }
}
